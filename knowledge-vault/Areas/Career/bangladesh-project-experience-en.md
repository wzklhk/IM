ZTE Corporation  Dept.
Main Project: Bangladesh CCN (Core Cloud Network) Project
Project Brief: The project aimed to virtualize and containerize the operator's core network, migrating legacy EPC/IMS network elements from dedicated hardware appliances to a cloud-native NFVI platform. The deployment spanned two major infrastructure pillars — Red Hat OpenShift (OCP) as the container platform for control-plane and user-plane workloads, and BlueCat DNS as the carrier-grade GI DNS system. The architecture was designed for high availability, horizontal scalability, and multi-tenant workload isolation, serving millions of mobile subscribers.

Responsibilities: As Cloud-Native Infrastructure Engineer, I was responsible for:

- Leading the end-to-end delivery of Red Hat OpenShift container platform, including LLD design, Underlay/Overlay network planning, switch/firewall commissioning, and OCP cluster installation (Hub/Service/STF clusters) on bare-metal servers.

- Deploying RHEL-based support infrastructure with KVM virtualization to provision Bastion, IDM (HA pair), and Load Balancer (HAProxy + Keepalived HA pair) nodes, ensuring no single point of failure in cluster bootstrap and management.

- Delivering BlueCat DNS system (BAM + BDDS HA architecture) on OpenStack virtualization, integrated as the GI DNS for core network Gi interface — the first-hop DNS for all mobile data traffic exiting the operator's network.

- Authoring platform deployment specifications, operational maintenance manuals, and conducting knowledge transfer to local O&M teams.

- Performing online expansion and resource rebalancing of worker nodes to accommodate growing core network workloads while maintaining service continuity.

Other projects or work:
Technical support for Core Network, Cloud-Native Infrastructure, and NFVI products across 40+ countries overseas, as well as in all 31 provinces of China.
