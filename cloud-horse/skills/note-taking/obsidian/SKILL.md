---
name: obsidian
description: Read, search, create, and edit notes in the Obsidian vault.
platforms: [linux, macos, windows]
---

# Obsidian Vault

Use this skill for filesystem-first Obsidian vault work: reading notes, listing notes, searching note files, creating notes, appending content, and adding wikilinks.

## Vault path

Use a known or resolved vault path before calling file tools.

The documented vault-path convention is the `OBSIDIAN_VAULT_PATH` environment variable, for example from `~/.hermes/.env`. If it is unset, check these common locations in order:
1. `~/workspace/knowledge-vault` (PARA-structured vault used by this user)
2. `~/Documents/Obsidian Vault` (generic fallback)

File tools do not expand shell variables. Do not pass paths containing `$OBSIDIAN_VAULT_PATH` to `read_file`, `write_file`, `patch`, or `search_files`; resolve the vault path first and pass a concrete absolute path. Vault paths may contain spaces, which is another reason to prefer file tools over shell commands.

If the vault path is unknown, `terminal` is acceptable for resolving `OBSIDIAN_VAULT_PATH` or checking whether the fallback path exists. Once the path is known, switch back to file tools.

## Read a note

Use `read_file` with the resolved absolute path to the note. Prefer this over `cat` because it provides line numbers and pagination.

## List notes

Use `search_files` with `target: "files"` and the resolved vault path. Prefer this over `find` or `ls`.

- To list all markdown notes, use `pattern: "*.md"` under the vault path.
- To list a subfolder, search under that subfolder's absolute path.

## Search

Use `search_files` for both filename and content searches. Prefer this over `grep`, `find`, or `ls`.

- For filenames, use `search_files` with `target: "files"` and a filename `pattern`.
- For note contents, use `search_files` with `target: "content"`, the content regex as `pattern`, and `file_glob: "*.md"` when you want to restrict matches to markdown notes.

## Create a note

Use `write_file` with the resolved absolute path and the full markdown content. Prefer this over shell heredocs or `echo` because it avoids shell quoting issues and returns structured results.

## Append to a note

Prefer a native file-tool workflow when it is not awkward:

- Read the target note with `read_file`.
- Use `patch` for an anchored append when there is stable context, such as adding a section after an existing heading or appending before a known trailing block.
- Use `write_file` when rewriting the whole note is clearer than constructing a fragile patch.

For an anchored append with `patch`, replace the anchor with the anchor plus the new content.

For a simple append with no stable context, `terminal` is acceptable if it is the clearest safe option.

## Targeted edits

Use `patch` for focused note changes when the current content gives you stable context. Prefer this over shell text rewriting.

## Wikilinks

Obsidian links notes with `[[Note Name]]` syntax. When creating notes, use these to link related content.

### Pre-Reply Vault Search (Long-Term Brain)

When the user has an established vault that's included in daily backup:

**Before replying to any user message**, search the vault for notes relevant to the current topic. The vault is curated long-term memory — past decisions, plans, domain knowledge. Do NOT load the entire vault (too large — may have 20+ files). Instead, use `search_files` with topic-relevant keywords to find matching notes, then read relevant ones.

This is a conversation-time behavior pattern: the vault is an external brain consulted proactively, not just a passive storage.

### Resume Content Architecture: Summary vs Detail

**User preference (2026-05-12):** Resume entries should be **technical summaries only**. Detailed technical content (architecture, component names, protocol details, deployment topology) goes into the **corresponding knowledge vault area**, not the resume itself.

**Pattern:**

- Resume `Areas/General/resume.md`:
  - One-liner per project summarizing role, tech stack, and deliverable scope
  - A `详见 [[Areas/Telecom/ProjectName]]` link to the deep-dive note
  - Example: *"基于 OpenStack 虚拟化平台搭建部署环境，部署 BAM + BDDS 架构，多 BDDS 节点交换机负载均衡。该系统作为 GI DNS 使用。详见 [[Areas/Telecom/Bluecat-DNS-delivery]]"*

- Knowledge vault detail `Areas/Telecom/ProjectName.md`:
  - Full architecture (BAM/BDDS, Hub/Service/STF OCP clusters)
  - Component lists with counts (e.g. "3 Master + 3 Infra + 几十 Worker")
  - Deployment topology (Support nodes, virsh VMs, network planes)
  - Protocol details (SIP, Diameter, GB/T 28181)
  - All the deep technical knowledge that proves expertise but clutters a resume

**When filling in project details for the user:**
- Start by asking for the project's architecture details
- Put specifics in a `Areas/Telecom/` or `Areas/Development/` note
- Only put a crisp 2-4 bullet summary in the resume itself
- Link resume → deep note via `详见 [[NoteName]]`

### Pitfalls

- **Don't treat the vault as a dump of everything** — save what's valuable, not every word
- **Don't write raw conversation logs** into the vault — synthesize, organize, and structure
- **Don't forget to update MOCs** — a note that can't be found is as good as not written
- **Don't sync to non-vault copies unprompted** — javase is for code, not resume syncing
