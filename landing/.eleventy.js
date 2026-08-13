module.exports = function (eleventyConfig) {
  eleventyConfig.addPassthroughCopy("css");
  eleventyConfig.addPassthroughCopy("js");
  eleventyConfig.addPassthroughCopy({ "../static": "/" });
  eleventyConfig.addPassthroughCopy({
    "node_modules/@docsearch/css/dist/style.css": "assets/docsearch.css",
  });
  eleventyConfig.addPassthroughCopy({
    "node_modules/@docsearch/js/dist/umd/docsearch.js": "assets/docsearch.js",
  });

  eleventyConfig.addFilter("downloadUrl", (tagVersion, version, ext) => {
    const base = "https://github.com/sbt/sbt/releases/download";
    return `${base}/v${tagVersion}/sbt-${version}${ext}`;
  });

  return {
    dir: {
      input: "src",
      includes: "../_includes",
      data: "../_data",
      output: "_site",
    },
    pathPrefix: "/",
    markdownTemplateEngine: "njk",
    htmlTemplateEngine: "njk",
  };
};
