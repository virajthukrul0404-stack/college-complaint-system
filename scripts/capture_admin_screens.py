import asyncio
import base64
import json
import os
import subprocess
import time
import requests
import websockets

EDGE_PATH = r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
BASE_URL = "http://localhost:8080"
SCREENSHOTS_DIR = r"C:\Users\viraj\Desktop\JavaMiniProject\screenshots"

async def main():
    os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

    # 1. Log in via requests to get session cookie
    session = requests.Session()
    login_page = session.get(f"{BASE_URL}/admin/login")
    import re
    m = re.search(r'name="csrfToken"\s+value="([^"]+)"', login_page.text)
    csrf_token = m.group(1) if m else ""

    login_res = session.post(f"{BASE_URL}/admin/login", data={
        "username": "superadmin",
        "password": "Admin@12345",
        "csrfToken": csrf_token
    })
    session_cookie = session.cookies.get("CAMPUS_SESSION_ID") or session.cookies.get("JSESSIONID")
    print(f"Logged in via requests! Session cookie: {session_cookie}")

    # Also log in a student session to capture student desktop complaints
    stu_sess = requests.Session()
    stu_get = stu_sess.get(f"{BASE_URL}/student/login")
    sm = re.search(r'name="csrfToken"\s+value="([^"]+)"', stu_get.text)
    stu_csrf = sm.group(1) if sm else ""
    stu_sess.post(f"{BASE_URL}/student/login", data={
        "csrfToken": stu_csrf,
        "identifier": "student1@campus.edu",
        "password": "Student@123"
    })
    stu_cookie = stu_sess.cookies.get("CAMPUS_SESSION_ID") or stu_sess.cookies.get("JSESSIONID")

    # 2. Launch headless Edge with remote debugging
    edge_proc = subprocess.Popen([
        EDGE_PATH,
        "--headless=new",
        "--remote-debugging-port=9222",
        "--disable-gpu",
        "--no-sandbox",
        "about:blank"
    ])
    time.sleep(2)

    try:
        ver = requests.get("http://localhost:9222/json/version").json()
        ws_url = ver["webSocketDebuggerUrl"]
        print("Connected to CDP:", ws_url)

        async with websockets.connect(ws_url) as ws:
            msg_id = 0
            async def send(method, params=None):
                nonlocal msg_id
                msg_id += 1
                payload = {"id": msg_id, "method": method}
                if params:
                    payload["params"] = params
                await ws.send(json.dumps(payload))
                while True:
                    res = json.loads(await ws.recv())
                    if res.get("id") == msg_id:
                        return res.get("result", {})

            # Create target page
            target = await send("Target.createTarget", {"url": "about:blank"})
            target_id = target["targetId"]

            # Connect to page target
            page_ws_url = f"ws://localhost:9222/devtools/page/{target_id}"
            async with websockets.connect(page_ws_url) as page_ws:
                page_id = 0
                async def send_page(method, params=None):
                    nonlocal page_id
                    page_id += 1
                    payload = {"id": page_id, "method": method}
                    if params:
                        payload["params"] = params
                    await page_ws.send(json.dumps(payload))
                    while True:
                        res = json.loads(await page_ws.recv())
                        if res.get("id") == page_id:
                            return res.get("result", {})

                await send_page("Network.enable")
                await send_page("Page.enable")

                # 1. Admin screens
                if session_cookie:
                    await send_page("Network.setCookie", {
                        "name": "CAMPUS_SESSION_ID",
                        "value": session_cookie,
                        "domain": "localhost",
                        "path": "/"
                    })
                    await send_page("Network.setCookie", {
                        "name": "JSESSIONID",
                        "value": session_cookie,
                        "domain": "localhost",
                        "path": "/"
                    })

                admin_screens = [
                    ("admin_dashboard_desktop.png", f"{BASE_URL}/admin/dashboard", 1440, 950, False),
                    ("admin_complaints_desktop.png", f"{BASE_URL}/admin/complaints", 1440, 950, False),
                    ("admin_detail_desktop.png", f"{BASE_URL}/admin/complaints/detail?id=1", 1440, 1100, False),
                    ("admin_feedback_desktop.png", f"{BASE_URL}/admin/feedback", 1440, 950, False),
                    ("admin_mobile_laptop_notice.png", f"{BASE_URL}/admin/dashboard", 390, 844, True)
                ]

                for filename, url, width, height, is_mobile in admin_screens:
                    print(f"Capturing {filename} at {width}x{height} from {url}...")
                    if is_mobile:
                        await send_page("Network.setUserAgentOverride", {
                            "userAgent": "Mozilla/5.0 (iPhone; CPU iPhone OS 16_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Mobile/15E148 Safari/604.1"
                        })
                    else:
                        await send_page("Network.setUserAgentOverride", {
                            "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                        })

                    await send_page("Emulation.setDeviceMetricsOverride", {
                        "width": width,
                        "height": height,
                        "deviceScaleFactor": 1,
                        "mobile": is_mobile
                    })
                    await send_page("Page.navigate", {"url": url})
                    await asyncio.sleep(2.0)

                    res = await send_page("Page.captureScreenshot", {"format": "png"})
                    data = base64.b64decode(res["data"])
                    filepath = os.path.join(SCREENSHOTS_DIR, filename)
                    with open(filepath, "wb") as f:
                        f.write(data)
                    print(f"Saved {filepath} ({len(data)} bytes)")

                # 2. Student desktop complaints screen
                if stu_cookie:
                    await send_page("Network.setCookie", {
                        "name": "CAMPUS_SESSION_ID",
                        "value": stu_cookie,
                        "domain": "localhost",
                        "path": "/"
                    })
                    await send_page("Network.setCookie", {
                        "name": "JSESSIONID",
                        "value": stu_cookie,
                        "domain": "localhost",
                        "path": "/"
                    })
                    await send_page("Network.setUserAgentOverride", {
                        "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                    })
                    await send_page("Emulation.setDeviceMetricsOverride", {
                        "width": 1440,
                        "height": 900,
                        "deviceScaleFactor": 1,
                        "mobile": False
                    })
                    await send_page("Page.navigate", {"url": f"{BASE_URL}/student/complaints"})
                    await asyncio.sleep(2.0)
                    res = await send_page("Page.captureScreenshot", {"format": "png"})
                    data = base64.b64decode(res["data"])
                    filepath = os.path.join(SCREENSHOTS_DIR, "student_desktop_complaints.png")
                    with open(filepath, "wb") as f:
                        f.write(data)
                    print(f"Saved {filepath} ({len(data)} bytes)")

    finally:
        edge_proc.terminate()
        edge_proc.wait()

if __name__ == "__main__":
    asyncio.run(main())
