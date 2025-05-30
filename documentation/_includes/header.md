[![Maven](https://img.shields.io/maven-central/v/{{ project["library"]["maven"] }}/{{ project["library"]["maven-main-library"] }}?style=for-the-badge&color=blue)](https://central.sonatype.com/namespace/{{ project["library"]["maven"] }}){:target="_blank"}
![API](https://img.shields.io/badge/api-{{ project["library"]["api"] }}%2B-brightgreen.svg?style=for-the-badge)
![Kotlin](https://img.shields.io/github/languages/top/{{ project["library"]["github"] }}.svg?style=for-the-badge&color=blueviolet)
{% if project["library"]["multiplatform"] -%}
![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&label=Kotlin)
{%- endif -%}
[![License](https://img.shields.io/github/license/{{ project["library"]["github"] }}?style=for-the-badge)](https://github.com/{{ project["library"]["github"] }}/blob/{{ project["library"]["branch"] }}/LICENSE){:target="_blank"}

<h1 align="center"><b>{{ project["library"]["name"] }}</b></h1>

![PLATFORMS](https://img.shields.io/badge/PLATFORMS-black?style=for-the-badge){:class=exclude-glightbox }
{% if "android" in project["library"]["platforms"] -%}
![Android](https://img.shields.io/badge/android-3DDC84?style=for-the-badge){:class=exclude-glightbox }
{% endif -%}
{% if "ios" in project["library"]["platforms"] -%}
![iOS](https://img.shields.io/badge/ios-007AFF?style=for-the-badge){:class=exclude-glightbox }
{% endif -%}
{% if "windows" in project["library"]["platforms"] -%}
![Windows](https://img.shields.io/badge/windows-00A4EF?style=for-the-badge){:class=exclude-glightbox }
{% endif -%}
{% if "macos" in project["library"]["platforms"] -%}
![iOS](https://img.shields.io/badge/macos-000000?style=for-the-badge){:class=exclude-glightbox }
{% endif -%}
{% if "linux" in project["library"]["platforms"] -%}
![Linux](https://img.shields.io/badge/linux-FCC624?style=for-the-badge){:class=exclude-glightbox }
{% endif -%}
{% if "wasm" in project["library"]["platforms"] -%}
![WASM](https://img.shields.io/badge/wasm-654FF0?style=for-the-badge){:class=exclude-glightbox }
{% endif -%}
{% if "js" in project["library"]["platforms"] -%}
![js](https://img.shields.io/badge/js-F7DF1E?style=for-the-badge){:class=exclude-glightbox }
{% endif -%}