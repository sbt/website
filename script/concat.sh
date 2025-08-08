#!/bin/bash

set -o errexit  # abort on nonzero exitstatus
set -o nounset  # abort on unbound variable
set -o pipefail # don't hide errors within pipes

locale=""

show_help() {
    echo "Usage: $0 <locale>"
}

if [[ $# -eq 0 ]]; then
    show_help
    exit 1
fi

while [[ $# -gt 0 ]]; do
    case "$1" in
        *)
            locale="$1"
            shift
            ;;
    esac
done

output_file="po/$locale.po"
cat po/summary/summary.$locale.po > "$output_file"
echo -e "\n" >> "$output_file"

for file in $(find po -name "*.$locale.po" -print | sort); do
    if [[ $file != "po/summary/summary.$locale.po" ]]; then
        # Append each file's content to the output file, starting from line 13
        tail -n +14 "$file" >> "$output_file"
        echo -e "\n" >> "$output_file"
    fi
done
