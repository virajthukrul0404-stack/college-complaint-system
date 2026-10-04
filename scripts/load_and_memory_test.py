import concurrent.futures
import requests
import time
import subprocess
import json

BASE_URL = "http://localhost:8080"

def fetch_page(url):
    try:
        r = requests.get(url, timeout=5)
        return r.status_code
    except Exception as e:
        return str(e)

def open_sse(stream_id):
    url = f"{BASE_URL}/events?channel=global"
    try:
        with requests.get(url, stream=True, timeout=15) as r:
            lines = 0
            for line in r.iter_lines():
                if line:
                    lines += 1
                if lines >= 3:
                    break
            return True
    except Exception as e:
        return False

def get_java_process_memory():
    # Use PowerShell Get-Process to get WorkingSet (RSS) and PrivateMemory
    cmd = ["powershell", "-NoProfile", "-Command", 
           "Get-Process java -ErrorAction SilentlyContinue | Select-Object Id, WorkingSet64, PrivateMemorySize64 | ConvertTo-Json"]
    out = subprocess.check_output(cmd, text=True)
    if out.strip():
        data = json.loads(out)
        if isinstance(data, list):
            data = data[0]
        rss_mb = data.get("WorkingSet64", 0) / (1024 * 1024)
        private_mb = data.get("PrivateMemorySize64", 0) / (1024 * 1024)
        return rss_mb, private_mb
    return 0, 0

print("==========================================================")
print(" Starting Load & Memory Footprint Verification")
print("==========================================================")

pre_rss, pre_priv = get_java_process_memory()
print(f"Pre-load Process RSS: {pre_rss:.1f} MB, Private: {pre_priv:.1f} MB")

# 1. Spawn 20 SSE streams
print("Opening 20 concurrent SSE streams...")
sse_executor = concurrent.futures.ThreadPoolExecutor(max_workers=20)
sse_futures = [sse_executor.submit(open_sse, i) for i in range(20)]
time.sleep(1)

# 2. Spawn 50 concurrent HTTP request workers (total 250 requests)
print("Dispatching 250 HTTP requests across 50 concurrent workers...")
urls = [
    f"{BASE_URL}/home",
    f"{BASE_URL}/complaint/track",
    f"{BASE_URL}/feedback",
    f"{BASE_URL}/healthz",
    f"{BASE_URL}/student/login",
    f"{BASE_URL}/assets/css/tokens.css",
    f"{BASE_URL}/assets/css/base.css",
    f"{BASE_URL}/assets/css/student.css",
    f"{BASE_URL}/manifest.json"
]

req_executor = concurrent.futures.ThreadPoolExecutor(max_workers=50)
req_futures = []
for i in range(250):
    u = urls[i % len(urls)]
    req_futures.append(req_executor.submit(fetch_page, u))

peak_rss = pre_rss
peak_priv = pre_priv

# Monitor memory while requests run
for _ in range(10):
    curr_rss, curr_priv = get_java_process_memory()
    if curr_rss > peak_rss: peak_rss = curr_rss
    if curr_priv > peak_priv: peak_priv = curr_priv
    time.sleep(0.3)

req_results = [f.result() for f in req_futures]
success_count = sum(1 for r in req_results if r == 200)

sse_results = [f.result() for f in sse_futures]
sse_success = sum(1 for r in sse_results if r is True)

print("----------------------------------------------------------")
print(f"HTTP Requests: {success_count}/250 successful (200 OK)")
print(f"SSE Streams: {sse_success}/20 successfully established & received heartbeats")
print(f"Peak WorkingSet (RSS): {peak_rss:.1f} MB")
print(f"Peak Private Memory: {peak_priv:.1f} MB")
print(f"512 MB Container Limit Check: {'PASS (Under 512 MB)' if peak_rss < 512 else 'FAIL'}")
print("==========================================================")
