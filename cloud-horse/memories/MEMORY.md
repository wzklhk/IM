备份: cron 60ebda1c7bf8 runs backup-hermes.sh 3AM daily → push to wzklhk/my-hermes-backup. vault-sync: cron 75c663ae5200每5min(静默).
§
JavaSE repo: ~/workspace/javase → wzklhk/javase (专用token). JAVASE only for project code.
§
Persona: 郝明瑞, breed=Hermes Agent, 马圈=番禺(腾讯云广州VPS). 主人: 魏开昊. 本地马兄弟: 郝明智 (shared GitHub backup repo horse-comm protocol).
§
ETF DB: ~/.hermes/etf_market_db.json (repaired, 41,496 records across 7 indices). Structure: {meta, indices{sp500,nasdaq100,djia,vix,sh000001,sh000300,sh000905}}. Also at ~/workspace/QuantBase/data/processed/market_db.json. DB can get truncated during write — repair by rfind last complete entry + close JSON.
§
双结构知识库: PARA(~workspace/knowledge-vault/,用户读写) + LLM Wiki(~workspace/knowledge-vault-llm/,Agent只读)。我查知识先查llm-wiki.json索引再读对应页。push后自动同步LLM。兜底cron每6h。Obsidian/PARA组织。
§
bun v1.3.13 at ~/.bun/bin/. gstack at ~/gstack (40+ AI eng skills). gbrain at ~/gbrain (42 skills, brain at ~/.gbrain/brain.pglite, PGLite engine). gstack linked to ~/.claude/skills/gstack for gbrain cross-discovery.
§
馬群: shared/horse-comm/ 与郝明智日报通信. 3AM cron内检查. processed.txt记录已读.
§
V2Ray: v2ray-reality(Xray 26.5.9,VLESS+REALITY,31086,PubKey=Ma0zckCBZ3A2M7lJ9LdrI1l3HBKPikFirwFl0MeLkDc,sid=e74325f1,SNI=www.microsoft.com)。UUID=8b6786db-586a-46fd-856d-4c3d437b3936。配置文件=~/v2ray-reality/config.json。旧v2ray(VMess+WS,10086)已删除。
§
R730二手服务器已采纳。用户有工作室，计划Proxmox部署Linux开发VM给团队(Java/Python/Go, SSH/VS Code Remote开发)。先用旧笔记本练手。Skills created: proxmox-virtualization. Patched: vps-service-deployment (VMess-WS share link + REALITY note).
§
全资产月报(6/10): 支付宝¥439,661(余额宝¥181K+基金¥226K+帮你投¥20K) + CMB¥215K + 交行¥101K + 中行¥14K + 公积金¥71K + 养老金¥18K + 医保¥6.8K = 合计约¥865K。四象限:要花26%/保险0%/生钱13%/保本61%。保险未配置。报告: ~/workspace/QuantBase/portfolio/report-2026-06-10.md
§
GP项目: Alamgir 是站点工程师，负责硬件安装和上电。SYL/JES 上电时间找他确认。HP服务器扩容阻塞在OCP 4.16→4.18升级之后，不急。Bogura(4 rack EXP:5A-1+3A-2+44hp+2msl+2csl+1oob) ZTE 8台已装完待HP。Sylhet(2 rack EXP:5A-1+3A-2+44hp+2msl+2csl+1oob + NEP:6A-1) 机柜+ZTE装完，电源pending infra。Jessore(1 rack EXP:8A-1+NEP:6A-1) 6台ZTE已装，机柜+8台pending infra。Cumilla(13A-1) 全部ZTE装完加电连线，待配置。
§
GP项目：PCRF/SPR割接定在6月16日，只割话段880170。具体站点待确认。