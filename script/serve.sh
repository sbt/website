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

script/concat.sh "$locale"
# MDBOOK_BOOK__LANGUAGE="$locale" mdbook serve -d "book/$locale"
MDBOOK_BOOK__LANGUAGE="$locale" mdbook build -d "book/$locale"
python3 -m http.server 3000 --directory "book/$locale"
