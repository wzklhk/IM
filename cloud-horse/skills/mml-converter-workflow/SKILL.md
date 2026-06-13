---
name: mml-converter-workflow
description: Workflow for developing and testing the MML converter toolset — bidirectional conversion between MML, Excel, CSV, and SQL
category: software-development
---

# MML Converter Development & Testing Workflow

## Prerequisites

- Python 3.8+ with `venv`
- Dependencies: `pip install openpyxl xlrd lxml pytest`

## Setup Pitfalls

### PEP 668 Protection (Ubuntu/Debian 23.04+)
`pip install` fails with "externally-managed-environment". **Always use a virtual environment:**
```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

### openpyxl Required for Excel
`mml2tabular` and `tabular2mml` both need `openpyxl`. The CLI has an `ensure_openpyxl()` guard that exits with a helpful message, but install it first.

### Run Script as Entry Point
The project has `run.sh` at the root for all common tasks. Use it instead of manual steps:
```bash
./run.sh              # Full: build frontend + start backend
./run.sh build        # Frontend only (npm install → npm run build)
./run.sh start        # Backend only (requires prior build)
./run.sh dev          # Dev mode (backend + Vue dev server)
```

## Development Workflow

### Web App Development

1. **Backend** (`converter/app.py`): Flask app with controller/service/dao layers. Run with `./run.sh dev` for hot-reload backend + Vue dev server (port 8080, proxies /api → :5000).
2. **Frontend** (`frontend/src/`): Vue 2 + Element UI, split into 6 components. Dev server at port 8080 with automatic /api proxying.
3. **Config**: Edit `converter/config.yaml` to change server port, debug mode, or database path.
4. **Vue config** (`frontend/vue.config.js`): `publicPath: '/static/'`, `outputDir: '../converter/static'`.

### Adding a New Converter Direction

1. **Check existing models**: If converting TO tabular (Excel/CSV), reuse `tabular.py`; if FROM tabular, reuse `tabular2mml.py`.
2. **Use shared utilities**: Import from `utils/` — never rewrite `quote_mml_value()` or `sanitize_table_name()`.
3. **CLI boilerplate**: Use existing CLI scripts in `converters/` as templates (argparse, output handling).
4. **Data model**: Return/accept `MmlDataSet` from `utils/table.py`.

### Adding a New Output Format

1. Add a writer method to `tabular.py` (or create a new format module under `utils/`).
2. In the converter script, detect format by file extension.
3. Add the new format round-trip test to the test suite.

## CLI Quirk: Mutually Exclusive Groups with Defaults

In `mml2tabular.py`, the `--excel`/`--csv`/`--both` argument group uses `add_mutually_exclusive_group()`. If `--excel` has `default=True`, the CLI help still shows it as optional. To make the help text clearer, set it up as:

```python
output_group = parser.add_mutually_exclusive_group()
output_group.add_argument('--csv', action='store_true', help='只生成CSV文件')
output_group.add_argument('--both', action='store_true', help='同时生成Excel和CSV文件')
# No explicit --excel flag; default behavior is Excel
# In code: if not args.csv and not args.both: generate_excel = True
```

This avoids confusing help output while keeping Excel as the default.

## Testing Workflow

Always test **bidirectional round-trip** — convert back and forth to verify no data loss:

```bash
cd ~/workspace/mml-manager/converter

# 1. Activate venv first
source .venv/bin/activate

# 2. Test MML → Excel → MML
python -m converters.mml_to_tabular test_sample.mml -o test.xlsx
python -m converters.tabular_to_mml test.xlsx -o roundtrip.mml
diff test_sample.mml roundtrip.mml  # Should match (order may differ)

# 3. Test MML → CSV → MML
python -m converters.mml_to_tabular test_sample.mml -o test.csv
python -m converters.tabular_to_mml test.csv -o roundtrip.mml

# 4. Test via Web UI
python app.py  # Then POST /api/import-mml with test_sample.mml

# 5. Run the test suite
source .venv/bin/activate  # if not already
python -m pytest tests/ -v

# 6. Quick module test  
python -c "from utils.mml import quote_mml_value; print(quote_mml_value('hello world'))"
python -c "from utils.table import MmlDataSet; d = MmlDataSet(); print('OK')"
```

## Sample MML Data (26 records for testing, covers edge cases)

Generate a file `test_sample.mml` with:

```python
sample = \"\"\"// SUBRACK - 包含中文和特殊字符
SET SUBRACK:ID=1,NAME="MGW-01",TYPE=MGW,VENDOR=Huawei,LOCATION="机房A";
SET SUBRACK:ID=2,NAME="MGW-02",TYPE=MGW,VENDOR=Huawei,LOCATION="机房B-主控";
SET SUBRACK:ID=3,NAME="RNC-01",TYPE=RNC,VENDOR="ZTE,Corp",LOCATION="深圳机房";

SET BOARD:ID=1,SUBRACK_ID=1,SLOT=1,TYPE=GOU,VERSION=V100R001,DESC="主控板";
SET BOARD:ID=2,SUBRACK_ID=1,SLOT=2,TYPE=GOU,VERSION=V100R001,DESC="业务板-1";
SET BOARD:ID=3,SUBRACK_ID=1,SLOT=3,TYPE=GOU,VERSION=V100R002,DESC="业务板-2";
SET BOARD:ID=4,SUBRACK_ID=2,SLOT=1,TYPE=GOU,VERSION=V100R001,DESC="主控板";
SET BOARD:ID=5,SUBRACK_ID=3,SLOT=1,TYPE=TCU,VERSION=V200R001,DESC="传输板";

SET LTE_CELL:ID=1,NAME="CELL-A",PCI=101,TAC=1,FREQ=1850.0,COVER_RADIUS=500,BAND="Band3";
SET LTE_CELL:ID=2,NAME="CELL-B",PCI=102,TAC=1,FREQ=1850.0,COVER_RADIUS=300,BAND="Band3";
SET LTE_CELL:ID=3,NAME="CELL-C",PCI=103,TAC=2,FREQ=2100.0,COVER_RADIUS=800,BAND="Band1";
SET LTE_CELL:ID=4,NAME="CELL-D",PCI=104,TAC=2,FREQ=2100.0,COVER_RADIUS=1000,BAND="Band1";
SET LTE_CELL:ID=5,NAME="CELL-E",PCI=105,TAC=3,FREQ=2600.0,COVER_RADIUS=200,BAND="Band7";

SET NBRRELATION:ID=1,SRC_CELL="CELL-A",DST_CELL="CELL-B",HOTHRESH=3;
SET NBRRELATION:ID=2,SRC_CELL="CELL-A",DST_CELL="CELL-C",HOTHRESH=5;
SET NBRRELATION:ID=3,SRC_CELL="CELL-B",DST_CELL="CELL-A",HOTHRESH=3;
SET NBRRELATION:ID=4,SRC_CELL="CELL-C",DST_CELL="CELL-D",HOTHRESH=2;
SET NBRRELATION:ID=5,SRC_CELL="CELL-D",DST_CELL="CELL-E",HOTHRESH=4;

SET CELLPARAM:ID=1,CELL_NAME="CELL-A",CIO=0.5,RSRP_BIAS=0;
SET CELLPARAM:ID=2,CELL_NAME="CELL-B",CIO=1.0,RSRP_BIAS=2;
SET CELLPARAM:ID=3,CELL_NAME="CELL-C",CIO=-3.0,RSRP_BIAS=-1,TX_POWER=43.2;
SET CELLPARAM:ID=4,CELL_NAME="CELL-D",CIO=0.0,RSRP_BIAS=0,TX_POWER=46.0;
SET CELLPARAM:ID=5,CELL_NAME="CELL-E",CIO=-1.5,RSRP_BIAS=1;
ADD CELLPARAM:ID=6,CELL_NAME="CELL-F",CIO=-3.0,REMARKS="特殊值,含逗号";
ADD CELLPARAM:ID=7,CELL_NAME="CELL-G",CIO=2.5,REMARKS="测试中文备注";
ADD CELLPARAM:ID=8,CELL_NAME="CELL-H",CIO=0.0,RSRP_BIAS=0,TX_POWER=40,TX_POWER_DYN_MIN=30,TX_POWER_DYN_MAX=45;
\"\"\"
```

**Edge cases covered:**
- 中文（机房A、深圳机房、测试中文备注）
- 含逗号的引号值（`"ZTE,Corp"`, `"特殊值,含逗号"`）
- 负数值（`CIO=-3.0`, `RSRP_BIAS=-1`）
- 混合命令类型（SET + ADD）
- 部分字段缺失的表（`CELLPARAM:ID=1` 无 TX_POWER）
- 数字开头的值（频率 `1850.0`）
- 多列宽表（CELLPARAM 有 8 列）

## Testing Tips

- Test with both `SET` and `ADD` command types
- Test with values containing spaces, commas, special chars
- Test with Chinese characters (e.g., "机房A", "备注，含逗号")
- Test with numeric-only strings (IPs, frequencies) — should NOT be quoted
- Test with different encodings in Excel/CSV
- Test all three output modes: `--excel` (default), `--csv`, `--both`
- Verify Excel content programmatically using `openpyxl` in a script
- After `--both` mode, verify the `.xlsx` and `_csv/` directory both contain correct, matching data
- CSV files use UTF-8 BOM (`utf-8-sig`) encoding for Excel compatibility

## Documenting Work

After testing, save a test report to `TEST_REPORT.md` in the converter directory, covering:
- Test data description (tables, records, edge cases)
- Each test command and its output
- Verification results (content inspection, round-trip)
- Known behavior quirks (e.g., ADD→SET in round-trip, triple-quoted commas)

## Docker Deployment Workflow

When deploying from a Chinese VPS, use the project's `docker-compose.yml`:

```bash
cd ~/workspace/mml-manager

# Build (uses npm/pip/apt mirrors automatically)
docker compose build

# Start on port 80
docker compose up -d

# Check logs
docker compose logs -f

# View the app at http://<vps-ip>:80
```

The Dockerfile already includes all three China mirrors (npm → npmmirror, pip → tuna, apt → tuna) and does a multi-stage build (Node 22 → Python 3.11 single container). No additional mirror config needed.

### Pitfalls

- **apt mirror format**: Debian Trixie (python:3.11-slim) uses deb822 format in `/etc/apt/sources.list.d/debian.sources`. The Dockerfile handles both old and new formats.
- **Frontend build output**: `vue.config.js` sets `outputDir: '../converter/static'`. In Docker, the builder stage expects `/app/converter/static/` — the Dockerfile's WORKDIR (`/app/frontend`) + outputDir (`../converter/static`) resolves to this path automatically.
- **Database persistence**: The `mml_data` volume is mounted at `/app/data`. Update `converter/config.yaml` to point `database.path` to `/app/data/mml_config.db`.
