import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'For Scala and Java',
    description: (
      <>
        sbt is built for Scala and Java projects. It is the build tool of choice
        for <a href="https://scalasurvey2023.virtuslab.com/">84.7%</a> of the Scala developers (2023).
        One of the examples of Scala-specific feature is the ability to <i>cross build</i> your
        project against multiple Scala versions.
      </>
    ),
  },
  {
    title: 'Typesafe and parallel',
    description: (
      <>
        <code>build.sbt</code> is a Scala-based DSL to express parallel processing task graph.
        Typos in <code>build.sbt</code> will be caught as a compilation error.
      </>
    ),
  },
  {
    title: 'Speedy iteration',
    description: (
      <>
        With Zinc incremental compiler and file watch (<code>~</code>),
        edit-compile-test loop is fast and incremental.
      </>
    ),
  },
  {
    title: 'Extensible',
    description: (
      <>
        Adding support for new tasks and platforms (like Scala.js) is
        as easy as writing <code>build.sbt</code>.
        Join <a href="https://www.scala-sbt.org/1.x/docs/Community-Plugins.html">100+ community-maintained plugins</a> to
        share and reuse sbt tasks.
      </>
    ),
  },
];

function Feature({title, description}) {
  return (
    <div className={clsx('col col--3')}>
      <div className="text--center">
      </div>
      <div className="text--left padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <h2>Why sbt?</h2>
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
