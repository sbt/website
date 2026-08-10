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

get_toml_value() {
    local key="$1"
    sed -nE "s/^${key} = \"(.*)\"\$/\1/p" book.toml
}

set_toml_title() {
    local value="$1"
    awk -v val="$value" '
        BEGIN { done = 0 }
        /^title = / && !done { print "title = \"" val "\""; done = 1; next }
        { print }
    ' book.toml > book.toml.tmp && mv book.toml.tmp book.toml
}

title_en="$(get_toml_value "title_en")"

new_title="$(get_toml_value "title_${locale:-en}")"
if [[ -z "$new_title" ]]; then
    new_title="$title_en"
fi

restore_title() {
    set_toml_title "$title_en"
}
trap restore_title EXIT

set_toml_title "$new_title"

script/concat.sh "$locale"
MDBOOK_BOOK__LANGUAGE="$locale" mdbook build -d "book/$locale"
