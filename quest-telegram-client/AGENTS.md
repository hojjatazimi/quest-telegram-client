# Codex Instructions For Quest Telegram Client

- Keep fake mode compiling and runnable at all times.
- Never commit secrets, `local.properties`, Telegram `api_id`, Telegram `api_hash`, phone numbers, auth codes, TDLib session files, TDLib databases, downloaded Telegram files, or user data.
- Prefer small, reviewable changes.
- Keep UI Quest-friendly: large text, generous spacing, high contrast, comfortable landscape layouts, and click targets of at least 48dp.
- Do not use official Telegram branding, logos, icons, or wording that implies affiliation.
- Keep real TDLib work behind the repository/interface boundary so fake mode remains independent.
- Do not add analytics to the MVP.
- Avoid logging sensitive values, including messages and TDLib payloads.
- Run formatting/build checks before committing when the local Android toolchain is available.
