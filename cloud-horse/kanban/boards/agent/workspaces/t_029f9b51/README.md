# File System Management Toolkit

A collection of tools for file operations, backup, system configuration,
and environment setup on Linux.

## Files

| File | Description |
|------|-------------|
| `fsm.sh` | File System Manager — multi-purpose CLI and interactive menu |
| `.bash_aliases` | Shell aliases for daily file operations, git, disk usage |
| `setup-env.sh` | Environment installer — installs tools, aliases, and configures bash |

## Quick Start

```bash
# 1. Install everything (fsm.sh → ~/fsm.sh, aliases → ~/.bash_aliases)
./setup-env.sh install

# 2. Reload your shell config
source ~/.bashrc

# 3. Try it out
fsm sys-info        # system overview
fsm                 # interactive menu
fsm clean .         # clean temp files in current dir
fsm snapshot ~/workspace/mml-manager   # create a dated tarball
fsm find-large 50   # files larger than 50MB
```

## fsm.sh Commands

| Command | Alias | Description |
|---------|-------|-------------|
| `fsm backup <src> [dest]` | — | rsync incremental backup |
| `fsm snapshot <dir>` | — | create dated .tar.gz snapshot |
| `fsm clean <dir>` | — | remove temp/log/__pycache__ files |
| `fsm du-rank [dir]` | `du` | show top-20 biggest directories |
| `fsm find-large [n]` | `fl` | find files > n MB (default 100) |
| `fsm find-dupes [dir]` | `dupes` | find duplicate files by SHA1 |
| `fsm git-archive [dir]` | `ga` | tar.gz repo excluding .git |
| `fsm sys-info` | `info` | print system overview |
| `fsm watch-dir <dir>` | `watch` | inotify monitor for new files |
| `fsm env-init` | `init-env` | print bashrc-compatible config |
| `fsm` | `menu` | interactive menu |

## Shell Aliases

Installed via `.bash_aliases` → sourced from `~/.bashrc`. Key aliases:

| Alias | Expansion |
|-------|-----------|
| `ll` | `ls -alFh` |
| `lt` | `ls -lart` |
| `lth` | `ls -lt \| head` |
| `dus` | `du -sh * \| sort -h` |
| `duf` | `du -sh * \| sort -rh` |
| `find-big` | find files > 100MB |
| `find-recent` | find files modified today |
| `ports` | `ss -tulanp` |
| `gs` | `git status -s` |
| `gl` | `git log --oneline --graph -20` |
| `gd` | `git diff --color` |
| `fsm` | `~/fsm.sh` |
| `ws` | `cd ~/workspace` |
| `now` | date with timestamp |
| `tree` | colored tree view (filters .git/node_modules) |
