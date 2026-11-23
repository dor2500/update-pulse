# UpTrack — Update Sources & Engines

UpTrack checks **543 curated services** (plus any custom trackers you add) directly from official upstream APIs, feeds, and release pages — with **no middleman backend server**. Every check runs directly from your device via [`SourceFetcher.kt`](app/src/main/java/com/dor2500/uptrack/data/network/SourceFetcher.kt).

---

## Summary by Source Engine

| Source Engine | Count | Upstream Endpoint / Mechanism | Used For |
| :--- | :---: | :--- | :--- |
| **GitHub Releases** | **316** | `https://api.github.com/repos/{owner}/{repo}/releases/latest` (or `?per_page=1` for pre-releases) | Open-source Android apps, Windows tools (WSL, PowerToys, UniGetUI, Files, .NET), AI/LLMs, Dev tools, Self-hosted, Security, Gaming & Emulation |
| **APKMirror RSS** | **154** | `https://www.apkmirror.com/apk/{publisher}/{app}/feed/` | Proprietary Android apps (Google & Pixel apps, WhatsApp, Telegram, Instagram, Browsers, Israeli/US/EU/UK/Asia regional apps) |
| **Official RSS / Atom Feeds** | **19** | Direct XML RSS / Atom feeds from vendors | Windows Insider/Experience/IT Pro/Dev/Sysinternals blogs, Android Beta & Security Bulletin, Linux Kernel, Arch Linux, Docker Desktop, Plex, Cloud/AI Status pages |
| **npm Registry** | **12** | `https://registry.npmjs.org/{package}/latest` | JavaScript/TypeScript packages & AI CLIs (`@anthropic-ai/claude-code`, `@openai/codex`, `@google/gemini-cli`, TypeScript, Next.js, React, Vite, etc.) |
| **PyPI** | **12** | `https://pypi.org/pypi/{package}/json` | Python packages (`openai`, `anthropic`, `google-genai`, `torch`, `fastapi`, `uv`, `ruff`, `yt-dlp`, `homeassistant`, etc.) |
| **Docker Hub** | **12** | `https://hub.docker.com/v2/repositories/{repo}/tags?page_size=15&ordering=last_updated` | Official container images (Nginx, PostgreSQL, Redis, Traefik, Portainer, Jellyfin, Plex, Home Assistant, etc.) |
| **GitLab Releases** | **8** | `https://gitlab.com/api/v4/projects/{encoded_path}/releases?per_page=1` | GitLab-hosted projects (Aurora Store, F-Droid Client, Inkscape, GitLab Runner/CLI, Organic Maps, Veloren, postmarketOS) |
| **JSON Endpoints** | **7** | `https://endoflife.date/api/{product}.json` (key `0.latest`) | OS lifecycle & latest stable release tracking (Windows 11, Windows Server, macOS, iOS/iPadOS, Ubuntu, Fedora, Debian) |
| **Webpage Regex Monitors** | **3** | Direct HTML fetch + version regex extraction | Microsoft Learn Windows 11 & Windows 10 Release Health OS Builds, Google Pixel Factory Images & OTA |
| **Total** | **543** | | |

---

## Detailed Breakdown of Official OS, Windows & Status Feeds

### Windows & OS Official Sources
- **Windows 11 Official Updates (OS Build):** `https://learn.microsoft.com/en-us/windows/release-health/windows11-release-information` *(Webpage Regex)*
- **Windows 10 Official Updates (OS Build):** `https://learn.microsoft.com/en-us/windows/release-health/release-information` *(Webpage Regex)*
- **Windows 11 Insider Preview:** `https://blogs.windows.com/windows-insider/feed/` *(RSS)*
- **Windows Experience Blog:** `https://blogs.windows.com/windowsexperience/feed/` *(RSS)*
- **Windows IT Pro & Patch Updates:** `https://techcommunity.microsoft.com/t5/s/gxcuf89792/rss/board?board.id=Windows-ITPro-blog` *(RSS)*
- **Windows Developer Blog:** `https://blogs.windows.com/windowsdeveloper/feed/` *(RSS)*
- **Microsoft Sysinternals:** `https://techcommunity.microsoft.com/t5/s/gxcuf89792/rss/board?board.id=Sysinternals-Blog` *(RSS)*
- **Windows 11 / Windows Server / macOS / iOS / Ubuntu / Fedora / Debian:** `https://endoflife.date/api/{os}.json` *(JSON API)*
- **Linux Kernel Releases:** `https://www.kernel.org/feeds/kdist.xml` *(RSS)*
- **Arch Linux News:** `https://archlinux.org/feeds/news/` *(RSS)*
- **Windows Platform & Utilities:** Official GitHub Releases (`microsoft/WSL`, `microsoft/WindowsAppSDK`, `dotnet/core`, `microsoft/edit`, `microsoft/PowerToys`, `microsoft/terminal`, `microsoft/winget-cli`, `marticliment/UniGetUI`, `Files-Community/Files`, `valinet/ExplorerPatcher`, `TranslucentTB/TranslucentTB`, `ChrisTitusTech/winutil`, `rocksdanister/lively`, `QL-Win/QuickLook`, `NVIDIA/open-gpu-kernel-modules`, `fwupd/fwupd`)

### Google & Android Official Sources
- **Google Pixel Factory Images & OTA:** `https://developers.google.com/android/ota` *(Webpage Regex)*
- **Android / Pixel Beta Program:** `https://android-developers.googleblog.com/feeds/posts/default` *(Atom)*
- **Android Security Bulletin:** `https://source.android.com/docs/security/bulletin/rss.xml` *(RSS)*

### Other Official Vendor & Status Feeds
- **Docker Desktop:** `https://docs.docker.com/desktop/release-notes/index.xml` *(RSS)*
- **Plex Media Server:** `https://forums.plex.tv/c/announcements/5.rss` *(RSS)*
- **NOAA / NHC Advisories:** `https://www.nhc.noaa.gov/xml/TWOAT.xml` *(RSS)*
- **Cloud & AI Status Feeds:**
  - GitHub Status — `https://www.githubstatus.com/history.rss`
  - Cloudflare Status — `https://www.cloudflarestatus.com/history.rss`
  - OpenAI Status — `https://status.openai.com/history.rss`
  - Anthropic (Claude) Status — `https://status.anthropic.com/history.rss`
  - Discord Status — `https://discordstatus.com/history.rss`
  - Vercel Status — `https://www.vercel-status.com/history.rss`

---

## Self-Updater Source
UpTrack's built-in self-updater checks `https://api.github.com/repos/dor2500/uptrack/releases/latest` directly and downloads the signed `.apk` asset from GitHub Releases.

## Privacy & Rate Limits
- All requests are sent directly from your phone with `User-Agent: UpTrack/<version> (Android)`.
- For GitHub Releases, GitHub allows 60 unauthenticated requests/hour per IP. You can optionally add a free personal GitHub token in **Settings → GitHub API Token** to raise the limit to 5,000 requests/hour.
