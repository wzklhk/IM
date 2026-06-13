---
name: proxmox-virtualization
description: Proxmox VE server deployment, VM lifecycle, nested virtualization (VMware for learning), KVM troubleshooting, cloud desktop/VDI planning, and architecture considerations (ARM vs x86). Covers both single-node and multi-node setups.
triggers:
  - "Proxmox"
  - "PVE"
  - "虚拟机"
  - "虚机"
  - "云桌面"
  - "VDI"
  - "KVM"
  - "虚拟化"
  - "跳板机"
  - "虚拟化宿主机"
  - "嵌套虚拟化"
  - "VT-x"
  - "AMD-V"
  - "BIOS 虚拟化"
category: devops
---

# Proxmox VE Virtualization

## Architecture overview

Proxmox VE is a Type 1 hypervisor based on Debian + KVM. It runs directly on hardware (no host OS), and supports both full VMs (KVM) and containers (LXC).

```
Hardware (CPU, RAM, disks)
   └── Proxmox VE (Debian + KVM kernel)
        ├── VM 101 (Linux dev machine)
        ├── VM 102 (Windows 10)
        └── ...
```

## Installation

### Bare metal (target: old laptop / server)

1. Download Proxmox VE 8.x ISO from proxmox.com
2. Write to USB with Rufus (select **DD Image mode**, not ISO) or `dd`
3. Boot from USB, follow GUI installer:
   - Target disk: whole disk (default)
   - Timezone: Asia/Shanghai
   - Set root password
   - Network: static IP recommended (e.g., `192.168.1.100/24`)
4. After reboot, access via `https://<IP>:8006`

### Post-install

```bash
# Switch to Tsinghua mirror (China VPS)
sed -i 's|http://download.proxmox.com|https://mirrors.tuna.tsinghua.edu.cn/proxmox|g' /etc/apt/sources.list.d/pve-enterprise.list
apt update && apt upgrade -y && reboot
```

### In VMware for learning (nested virtualization)

Proxmox can run inside VMware Workstation/Fusion for learning. **Does not need bare metal.**

1. Create a Linux → Debian 12.x 64-bit VM
2. CPU: 2 cores, 4GB RAM, 32GB disk
3. **Critical: enable nested virtualization** in VM settings:
   - VMware Workstation: Processors → ✅ Virtualize Intel VT-x/EPT or AMD-V/RVI
   - VMware Fusion: Processors & Memory → Advanced → ✅ Enable hypervisor applications
4. Boot from Proxmox ISO, install normally
5. Access via `https://<VM_IP>:8006` from host browser

> ⚠️ Nested KVM is slow. Good for UI learning, but don't expect to run usable VMs inside the nested Proxmox.

## KVM virtualization error: "TASK ERROR: KVM virtualisation configured, but not available"

This means the hypervisor doesn't have CPU virtualization support enabled. Fixes:

- **Bare metal**: Reboot → BIOS → Enable **Intel VT-x** / **AMD SVM Mode** → Save & exit
- **VMware**: Edit VM settings → Processors → Enable virtualization
- **Cloud VPS**: Most cloud VPS (Tencent, Alibaba) do NOT support nested KVM — Proxmox won't work there

Verify virtualization is available:
```bash
grep -cE '(vmx|svm)' /proc/cpuinfo   # > 0 means supported
kvm-ok                               # if installed: sudo apt install cpu-checker
```

## Creating a VM

### Web UI (recommended)

1. Left sidebar → right-click node → **Create VM**
2. General: set VM ID (auto), name
3. OS: select uploaded ISO (upload via **local → ISO Images** first)
4. System: VirtIO SCSI + ✅ Qemu Agent (for IP/management visibility)
5. Disks: VirtIO Block, size 20-80GB, Write Back cache
6. CPU: 2-4 cores, type **host** (passes real CPU features to VM)
7. Memory: 4096-8192 MB, ✅ Ballooning (elastic allocation)
8. Network: vmbr0 bridge, VirtIO model

### CLI

```bash
# Create VM with disk
qm create 100 --name dev-vm-01 --memory 4096 --cores 2 \
  --net0 virtio,bridge=vmbr0 \
  --scsihw virtio-scsi-pci \
  --boot order=scsi0

# Import an existing disk image (raw/qcow2)
qm importdisk 100 /var/lib/vz/template/iso/ubuntu-dev.qcow2 local-lvm
qm set 100 --scsi0 local-lvm:vm-100-disk-0

# Start / Stop / Restart
qm start 100
qm stop 100
qm reboot 100

# Snapshot
qm snapshot 100 init-setup
qm rollback 100 init-setup
```

### Post-install in VM

```bash
# Install QEMU Guest Agent (required for Proxmox to see IP/status)
sudo apt update && sudo apt install qemu-guest-agent -y
sudo systemctl enable --now qemu-guest-agent
```

## Architecture: ARM vs x86

**ARM images (Raspberry Pi, etc.) CANNOT run on x86 Proxmox.** The architectures are incompatible — KVM needs matching CPU architecture for hardware acceleration.

Workarounds (limited utility):
- **Extract files only**: `guestmount -a pi.img -m /dev/sda2 /mnt/pi` to copy out data
- **QEMU system emulation**: software-emulate ARM (abysmal performance, 5-10% of native)
- **Best approach**: replicate the environment natively on an x86 Linux VM

Proxmox on ARM (like Raspberry Pi 4/5 running Proxmox) is not officially supported and lacks many features. Use x86 hardware for production.

## Resource planning

### Memory requirements (Linux VM, development workloads)

| Use case | Per-VM recommendation | 128GB host max |
|---------|----------------------|---------------|
| Light dev (SSH + terminal) | 2 vCPU + 2-4 GB | 25-30 VMs |
| Full dev (VS Code Remote, Docker) | 2-4 vCPU + 4-8 GB | 15-20 VMs |
| Windows jump-box (RDP) | 2-4 vCPU + 4-8 GB | 10-15 VMs |
| Heavy dev (IDEs, databases) | 4-8 vCPU + 8-16 GB | 6-8 VMs |

### Hardware recommendations

| Scenario | Hardware | Estimated cost | VMs |
|---------|---------|---------------|-----|
| 1-3 dev VMs (learning) | Old laptop 8-16GB | Free | 1-3 |
| 5-10 dev VMs (studio) | Dell R730: 2xE5-2680v4 + 128GB | ¥2500-3500 | 10-15 |
| 10-20 dev VMs (office) | Dual Xeon + 256GB+ | ¥5000-8000 | 15-25 |
| Cloud desktop (Windows) | Self-built: i5-13500 + 64GB+ | ¥4000-6000 | 6-10 (Windows) |

### Storage

- **System**: 2x SSD in RAID1 (for Proxmox + ISOs)
- **VM disks**: 4x SSD in RAID10 or ZFS mirror (IOPS critical)
- **Backup**: separate NAS/disk target for vzdump

## Workload types

### Linux development VM (Java/Python/Go)

Pre-installed tooling per VM:
```bash
# Via a bootstrap script
curl -s "https://get.sdkman.io" | bash          # Java/Gradle/Maven
curl -fsSL https://deb.nodesource.com/setup_20.x | bash -  # Node.js
sudo apt install -y golang-go                     # Go
sudo apt install -y python3-pip python3-venv      # Python
sudo apt install -y docker.io                     # Docker (test envs)
```

### Docker inside VM for ephemeral test environments

Each developer runs their own Docker inside their VM for isolated test clusters. Or share a centralized test VM running Docker Compose with common services (MySQL, Redis, Kafka, Minio).

## Pitfalls

### ⚠️ UEFI vs BIOS in VM settings
Proxmox defaults to UEFI for new VMs. Some lightweight OS images (Alpine, some custom builds) expect BIOS/SeaBIOS. If the VM doesn't boot after creation, try changing to BIOS in Hardware settings.

### ⚠️ VM disk location
`local` (directory) vs `local-lvm` (LVM-thin). `local-lvm` is faster but cannot store ISO/template files — only VM disks. Upload ISOs to `local` storage.

### ⚠️ No snapshot for raw disks formatted as qcow2
If a VM disk is stored on a directory (not LVM-thin), snapshots work for qcow2 format. If stored on LVM-thin, snapshots use LVM snapshots (fast, efficient).

### ⚠️ Proxmox has no desktop environment
After installation, the server boots to a terminal. All management is via the web UI at `:8006` or SSH. Do not try to install a desktop — it defeats the purpose.

### ⚠️ Old laptops as 24/7 servers
Not ideal for production. Laptop cooling is poor, fans fail over time, and the battery is a fire risk if kept plugged in 24/7. For anything beyond learning, get a proper server or build a quiet tower.
