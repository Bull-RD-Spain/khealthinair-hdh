# Third-Party Licenses / Notices

This file documents third-party components included within this repository
whose licenses are independent from the project's main license (GNU GPLv3).

---

## `hdh-cdr/` (HAPI FHIR JPA Server Starter overlay)

**Origin:** The `hdh-cdr` image is built from the
[HAPI FHIR JPA Server Starter](https://github.com/hapifhir/hapi-fhir-jpaserver-starter)
(tag `image/v7.6.0`), which is cloned at build time by `hdh-cdr/Dockerfile`.
The following files in this repository are modified copies of files from
that project (or from the HAPI FHIR testpage overlay it uses), adapted for
K-HEALTHinAIR:

- `hdh-cdr/src/main/resources/application7.6.0.yaml`
  (from `src/main/resources/application.yaml`)
- `hdh-cdr/src/main/webapp/WEB-INF/templates/about.html`
- `hdh-cdr/src/main/webapp/WEB-INF/templates/tmpl-banner.html`
- `hdh-cdr/src/main/webapp/WEB-INF/templates/tmpl-footer.html`
- `hdh-cdr/src/main/webapp/WEB-INF/templates/tmpl-head.html`
- `hdh-cdr/src/main/webapp/WEB-INF/templates/tmpl-home-welcome.html`
- `hdh-cdr/src/main/webapp/WEB-INF/templates/welcome.html`
- `hdh-cdr/src/main/webapp/WEB-INF/templates/window-title.html`

**License of the source material:** Apache License, Version 2.0.

- Copyright: University Health Network / Smile Digital Health and the HAPI FHIR contributors
- Full license text: https://www.apache.org/licenses/LICENSE-2.0

**License of the modifications:** the modifications made by Bull SAS
(Copyright © 2021-2026 Bull SAS) are distributed under GPL-3.0-or-later.
The combined files are therefore marked
`SPDX-License-Identifier: GPL-3.0-or-later AND Apache-2.0`, and the original
Apache-2.0 terms continue to apply to the upstream portions.
