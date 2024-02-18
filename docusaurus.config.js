// @ts-check
// `@type` JSDoc annotations allow editor autocompletion and type checking
// (when paired with `@ts-check`).
// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config

import {themes as prismThemes} from 'prism-react-renderer';

import fs from 'fs';

import { parseFileContentFrontMatter } from '@docusaurus/utils/lib/markdownUtils';

/** Return a Map of routes to directs, derived from `slug` field in front-matter.*/
const slugsInDir = (dir) => {
  /** the routes of pages in dir, array of [route, path] */
  const _srcPagesRoutes =
    fs.readdirSync(dir)
      .filter(f => f.endsWith('.md') || f.endsWith('.mdx'))
      .map(f => [`/${f.replace(/\.mdx?$/, '')}`, `${dir}/${f}`])

  /** the slugs of routes in dir, array of [route, slug?] */
  const _routeSlugs = _srcPagesRoutes.map(r => {
    const [route, path] = r;
    const {frontMatter} = parseFileContentFrontMatter(fs.readFileSync(path, 'utf8'));
    return [route, frontMatter['slug']];
  });

  /** the map of routes to valid slugs */
  const routeSlugsMap = new Map();
  _routeSlugs.forEach(r => {
    const [route, slug] = r;
    if (slug !== undefined) {
      routeSlugsMap.set(route, slug);
    }
  });
  return routeSlugsMap;
}

const pagesRedirects = slugsInDir('src/pages');


/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'sbt',
  tagline: 'A simple build tool',
  favicon: 'assets/favicon.ico',

  // Set the production url of your site here
  url: 'https://www.scala-sbt.org/',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'sbt', // Usually your GitHub org/user name.
  projectName: 'website', // Usually your repo name.

  onBrokenLinks: 'warn',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        theme: {
          customCss: './src/css/custom.css',
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      // Replace with your project's social card
      image: 'img/docusaurus-social-card.jpg',
      navbar: {
        // title: 'sbt',
        logo: {
          alt: 'sbt logo',
          src: 'assets/sbt-logo.svg',
        },
        items: [
          {
            href: '/learn',
            label: 'Learn',
            position: 'left',
          },
          {
            href: '/download',
            label: 'Download',
            position: 'left',
          },
          {
            href: '/community',
            label: 'Get Involved',
            position: 'left',
          },
          {
            href: 'https://www.scala-sbt.org/2.x/docs/en/',
            label: 'Doc Beta',
            position: 'right',
          },
          {
            href: 'https://github.com/sbt/website',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'sbt 1.x docs',
                to: 'https://www.scala-sbt.org/1.x/docs/',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Discord',
                href: 'https://discord.com/channels/632150470000902164/922600050989875282',
              },
              {
                label: 'Stack Overflow',
                href: 'https://stackoverflow.com/questions/tagged/sbt',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/sbt/sbt',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Scala Center. Built with Docusaurus.`,
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
      },
      algolia: {
        // The application ID provided by Algolia
        appId: 'U55AU5GMCN',
        // Public API key: it is safe to commit it
        apiKey: '45ef8a0285275e68a1c09ecdfff2be0f',
        indexName: 'scala-sbt',
        contextualSearch: false,
        // treat scala-sbt.org as an external site until
        // we index 2.x documentations
        externalUrlRegex: 'www\.scala\-sbt\.org|scala\-sbt\.org',
      },

    }),

  plugins: [
    [
      '@docusaurus/plugin-client-redirects',
      {
        createRedirects(existingPath) {
          // create download.html, learn.html, community.html
          if (pagesRedirects.has(existingPath)) {
            return [pagesRedirects.get(existingPath)];
          }
          return undefined;
        }
      },
    ]
  ]
};

export default config;
