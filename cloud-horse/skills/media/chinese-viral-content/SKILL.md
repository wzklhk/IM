---
name: chinese-viral-content
title: Chinese Viral Content Discovery
description: Find and download viral Chinese content (memes, videos, GIFs) from platforms like Bilibili, Baidu Images, and Douyin/TikTok equivalents.
tags: [chinese, memes, videos, bilibili, baidu, content-discovery]
version: 1.0
---

# Chinese Viral Content Discovery

Use this skill when the user asks for Chinese viral content, memes, trending videos, or specific internet culture references from Chinese platforms.

## Platforms Covered

1. **Bilibili (哔哩哔哩)** - Video sharing platform, similar to YouTube
2. **Baidu Images (百度图片)** - Image search engine
3. **Weibo (微博)** - Microblogging platform (not covered in detail here)
4. **Douyin/TikTok** - Short video platform (access via web search)

## Workflow

### 1. Bilibili Video Search
```bash
# Navigate to Bilibili
browser_navigate("https://www.bilibili.com")

# Search for content (search box is usually @e9)
browser_type("@e9", "搜索关键词")
browser_press("Enter")

# Wait for search results, then click on first video (usually @e18)
browser_click("@e18")

# On video page, play button is usually @e12
browser_click("@e12")

# Get video thumbnail from images list
browser_get_images()
# Thumbnail URLs typically contain "archive" and end with .jpg@...webp
# Example: https://i2.hdslb.com/bfs/archive/7dbd4265e9fc8b6148467a7524b7c68b17eab78b.jpg@518w_290h_1c_!web-video-share-cover.webp
```

**Challenges with Bilibili:**
- Video URLs are complex and often embedded
- Direct download with yt-dlp may fail due to authentication
- **Alternative**: Download thumbnails and related content instead

### 2. Download Bilibili Thumbnails
```bash
# Get images from page
browser_get_images()

# Download specific thumbnail (usually the first or second image)
curl -s -o "thumbnail.jpg" "https://i2.hdslb.com/bfs/archive/..."
```

### 3. Baidu Images for Memes
```bash
# Navigate to Baidu Images
browser_navigate("https://image.baidu.com")

# Search for memes (search box is usually @e3)
browser_type("@e3", "关键词 表情包")
browser_press("Enter")

# Wait for results, then look for GIFs in the search results
# First GIF result is usually around @e14-@e16
# Download GIFs from common Chinese CDNs:
# Sinaimg: https://tva3.sinaimg.cn/large/...
# Example: https://tva3.sinaimg.cn/large/ceeb653ely8gpu3pdfoj2g20a009345m.gif
# 588ku: https://wimg.588ku.com/gif620/...
# Example: https://wimg.588ku.com/gif620/21/06/08/774cb08f1fa39fc7cbd144e06d809b87.gif
```

### 4. Common Image Sources
- **Sinaimg (新浪图床)**: `https://tva3.sinaimg.cn/large/...`
- **Bilibili CDN**: `https://i2.hdslb.com/bfs/archive/...`
- **Weibo CDN**: `https://wx2.sinaimg.cn/bmiddle/...`

## Pitfalls & Solutions

1. **Video Download Failure**
   - **Problem**: yt-dlp may not work due to platform restrictions or authentication
   - **Solution**: Focus on thumbnails, screenshots, and related images
   - Use `browser_vision()` for screenshots when video analysis is needed
   - **Alternative**: Use `browser_get_images()` to find thumbnails and previews

2. **Authentication Requirements**
   - Some Bilibili content requires login for full video playback
   - Look for publicly accessible thumbnails and previews (usually in search results)
   - Thumbnail URLs often contain "@" symbols with dimensions (e.g., @518w_290h)

3. **GIF vs Static Images**
   - Chinese memes often use GIFs with moving text/characters
   - Look for `.gif` extensions in image URLs
   - Common sources: Sinaimg (tva3.sinaimg.cn), 588ku (wimg.588ku.com)
   - Check file size: GIFs are usually larger (>50KB)

4. **Search Terms Optimization**
   - Use Chinese keywords in simplified Chinese
   - Add "表情包" for memes/stickers
   - Add "原版" for original versions
   - Add "高清" for high quality
   - Add "动图" or "GIF" specifically for animated content

5. **Browser Automation Challenges**
   - **Ref IDs change**: Element references (@e1, @e2, etc.) may vary between page loads
   - **Solution**: Use `browser_snapshot()` to inspect current page structure
   - **Timeout issues**: Some pages load slowly, increase timeout or retry

## Example: "什么是快乐星球" (What is Happy Planet) - Complete Workflow

1. **Cultural Context**: Explain this is a viral meme from children's TV show "Happy Planet", popularized by Ma Jiaqi (时代少年团 member)

2. **Bilibili search**: 
   ```bash
   browser_navigate("https://www.bilibili.com")
   browser_type("@e9", "什么是快乐星球")
   browser_press("Enter")
   browser_click("@e18")  # First result: "什么是快乐星球 原版高清"
   browser_click("@e12")  # Play button
   browser_get_images()
   # Download thumbnail: https://i2.hdslb.com/bfs/archive/7dbd4265e9fc8b6148467a7524b7c68b17eab78b.jpg@518w_290h_1c_!web-video-share-cover.webp
   ```

3. **Baidu Images search for memes**:
   ```bash
   browser_navigate("https://image.baidu.com")
   browser_type("@e3", "什么是快乐星球 表情包")
   browser_press("Enter")
   # Download popular memes:
   # 1. Panda head GIF: https://tva3.sinaimg.cn/large/ceeb653ely8gpu3pdfoj2g20a009345m.gif
   # 2. Saturn personification: https://wimg.588ku.com/gif620/21/06/08/774cb08f1fa39fc7cbd144e06d809b87.gif
   ```

4. **Deliver to user**:
   - Share thumbnail: MEDIA:/tmp/快乐星球封面.jpg
   - Share GIFs: MEDIA:/tmp/快乐星球表情包1.gif, MEDIA:/tmp/快乐星球表情包2.gif
   - Provide explanation of the meme's origin and cultural significance

## Verification
- Check file sizes: GIFs should be >50KB, images >5KB
- Confirm Chinese characters in filenames/content
- Share with user via MEDIA: paths

## Related Skills
- `gif-search` (for Tenor GIFs)
- `youtube-content` (for YouTube videos)
- `xitter` (for Twitter/X content)