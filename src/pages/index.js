import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import HomepageTestimonials from '@site/src/components/HomepageTestimonials';

import Heading from '@theme/Heading';
import styles from './index.module.css';

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
          {siteConfig.title}
        </Heading>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link
            className="button button--secondary button--lg"
            to="/docs/intro">
            Docusaurus Tutorial
          </Link>
        </div>
      </div>
    </header>
  );
}

function SimpleThings() {
  const {siteConfig} = useDocusaurusContext();
  return (
<div className="container">
<h2>Simple things easy</h2>
<h3>Hello, world!</h3>
<pre><code className="language-scala prettyprint"><span className="typ">ThisBuild</span> / scalaVersion := <span className="str">"3.3.1"</span>
</code></pre>
You just need one line of <code>build.sbt</code> to get started with Scala. Learn more on <a href="1.x/docs/sbt-by-example.html">sbt by Example</a> page.

<h3>sbt new</h3>
Choose from community-maintained <a href="https://github.com/search?o=desc&p=1&q=g8&s=stars&type=Repositories">Giter8 templates</a> to jump start your project:

<pre><code className="prettyprint">$ sbt new scala/scala-seed.g8<br></br>
$ sbt new playframework/play-scala-seed.g8<br></br>
$ sbt new akka/akka-http-quickstart-scala.g8<br></br>
$ sbt new http4s/http4s.g8<br></br>
$ sbt new holdenk/sparkProjectTemplate.g8<br></br>
</code></pre>
</div>
  );
}

export default function Home() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={`${siteConfig.title}, the simple build tool`}
      description="sbt is the interactive build tool for Scala, Java, and more. Define your tasks in Scala. Run them in parallel from the interactive shell.">
      <HomepageHeader />
      <main>
        <SimpleThings />
        <HomepageFeatures />
        <HomepageTestimonials />
      </main>
    </Layout>
  );
}
