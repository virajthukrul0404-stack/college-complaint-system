import asyncio
import base64
import json
import os
import re
import subprocess
import time
import requests
import websockets

EDGE_PATH = r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
BASE_URL = "http://localhost:8080"
SCREENSHOTS_DIR = r"C:\Users\viraj\Desktop\JavaMiniProject\screenshots"

async def capture_set(ws_url, session_cookie, cookie_name, screens):
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

        target = await send("Target.createTarget", {"url": "about:blank"})
        target_id = target["targetId"]
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

            if session_cookie:
                await send_page("Network.setCookie", {
                    "name": cookie_name,
                    "value": session_cookie,
                    "domain": "localhost",
                    "path": "/"
                })

            for filename, url, width, height, user_agent in screens:
                print(f"Capturing {filename} ({width}x{height}) from {url}...")
                if user_agent:
                    await send_page("Network.setUserAgentOverride", {"userAgent": user_agent})
                else:
                    await send_page("Network.setUserAgentOverride", {
                        "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                    })

                await send_page("Emulation.setDeviceMetricsOverride", {
                    "width": width,
                    "height": height,
                    "deviceScaleFactor": 1,
                    "mobile": (width <= 768)
                })
                await send_page("Page.navigate", {"url": url})
                await asyncio.sleep(2.0)  # Wait for rendering, charts, animations

                res = await send_page("Page.captureScreenshot", {"format": "png"})
                data = base64.b64decode(res["data"])
                filepath = os.path.join(SCREENSHOTS_DIR, filename)
                with open(filepath, "wb") as f:
                    f.write(data)
                print(f"Saved {filepath} ({len(data)} bytes)")

        await send("Target.closeTarget", {"targetId": target_id})

async def main():
    os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

    # 1. Student Session
    student_session = requests.Session()
    stu_login_get = student_session.get(f"{BASE_URL}/student/login")
    m = re.search(r'name="csrfToken"\s+value="([^"]+)"', stu_login_get.text)
    stu_csrf = m.group(1) if m else ""
    student_session.post(f"{BASE_URL}/student/login", data={
        "csrfToken": stu_csrf,
        "identifier": "student1@campus.edu",
        "password": "Student@123"
    })
    student_cookie = student_session.cookies.get("CAMPUS_SESSION_ID") or student_session.cookies.get("JSESSIONID")
    print(f"Student logged in. Cookie: {student_cookie}")

    # 2. Admin Session
    admin_session = requests.Session()
    adm_login_get = admin_session.get(f"{BASE_URL}/admin/login")
    m = re.search(r'name="csrfToken"\s+value="([^"]+)"', adm_login_get.text)
    adm_csrf = m.group(1) if m else ""
    admin_session.post(f"{BASE_URL}/admin/login", data={
        "csrfToken": adm_csrf,
        "username": "superadmin",
        "password": "Admin@12345"
    })
    admin_cookie = admin_session.cookies.get("CAMPUS_SESSION_ID") or admin_session.cookies.get("JSESSIONID")
    print(f"Admin logged in. Cookie: {admin_cookie}")

    # 3. Launch headless Edge with remote debugging
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

        iphone_ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Mobile/15E148 Safari/604.1"
        ipad_ua = "Mozilla/5.0 (iPad; CPU OS 16_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Mobile/15E148 Safari/604.1"
        desktop_ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

        # Public & Student Screens
        student_screens = [
            ("student_mobile_login.png", f"{BASE_URL}/student/login", 390, 844, iphone_ua),
            ("student_mobile_home.png", f"{BASE_URL}/student/home", 390, 844, iphone_ua),
            ("student_mobile_form.png", f"{BASE_URL}/student/complaint/new", 390, 844, iphone_ua),
            ("student_mobile_complaints.png", f"{BASE_URL}/student/complaints", 390, 844, iphone_ua),
            ("student_tablet_home.png", f"{BASE_URL}/home", 768, 1024, ipad_ua),
            ("student_desktop_form.png", f"{BASE_URL}/student/complaint/new", 1440, 900, desktop_ua),
            ("student_desktop_complaints.png", f"{BASE_URL}/student/complaints", 1440, 900, desktop_ua),
            ("admin_mobile_laptop_notice.png", f"{BASE_URL}/admin/dashboard", 390, 844, iphone_ua),
        ]
        cookie_key = "CAMPUS_SESSION_ID" if "CAMPUS_SESSION_ID" in student_session.cookies else "JSESSIONID"
        await capture_set(ws_url, student_cookie, cookie_key, student_screens)

        # Admin Screens
        admin_screens = [
            ("admin_desktop_dashboard.png", f"{BASE_URL}/admin/dashboard", 1440, 950, desktop_ua),
            ("admin_desktop_complaints.png", f"{BASE_URL}/admin/complaints", 1440, 950, desktop_ua),
            ("admin_desktop_detail.png", f"{BASE_URL}/admin/complaints/detail?id=1", 1440, 1100, desktop_ua),
            ("admin_desktop_feedback.png", f"{BASE_URL}/admin/feedback", 1440, 950, desktop_ua),
        ]
        adm_cookie_key = "CAMPUS_SESSION_ID" if "CAMPUS_SESSION_ID" in admin_session.cookies else "JSESSIONID"
        await capture_set(ws_url, admin_cookie, adm_cookie_key, admin_screens)

    finally:
        edge_proc.terminate()
        edge_proc.wait()

if __name__ == "__main__":
    asyncio.run(main())
