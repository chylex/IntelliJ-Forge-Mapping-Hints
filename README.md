This IntelliJ IDEA plugin adds hints next to SRG names (`field_...`, `func_...`, `p_...`) based on suggestions in a CSV file.

The CSV must follow several rules:

- Requires header line
- Columns:
    - `validated` (`TRUE`/`FALSE`)
    - `class name` (string)
    - `unmapped name` (string)
    - `mapped name` (string)
    - `comment` (string, optional)
- No quote parsing or escaping, the first 4 commas must be delimiters (`comment` column contents can include commas)

Then, opening a Java file automatically adds inlay hints of several types:

- **Validated** (green/blue) names are those for which the `validated` property equals `TRUE`
- **Suggested** (yellow) names are those for which the `validated` property equals something else
- **Empty** (gray) are entries which exist in the file but their `mapped name` is empty
- **Missing** (gray) are entries which are SRG names but do not match any `unmapped name` in the file

![Example (Green Highlight)](https://github.com/chylex/IntelliJ-Forge-Mapping-Hints/blob/master/.github/readme/example-green.png)

![Example (Blue Highlight)](https://github.com/chylex/IntelliJ-Forge-Mapping-Hints/blob/master/.github/readme/example-blue.png)

# Settings

View `Settings > Editor > Inlay Hints > Java > Minecraft Forge mapping suggestions` to access the settings.

![Settings](https://github.com/chylex/IntelliJ-Forge-Mapping-Hints/blob/master/.github/readme/settings.png)
