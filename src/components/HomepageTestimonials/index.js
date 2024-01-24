import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

const TestimonialList = [
  {
    person: 'Keyur Shah, Associate Fellow, Verizon',
    description: (
      <>
        Previously 21% of the developer's time was spent in redeploying
        WebLogic application, which was unacceptable. We had to find a solution:
        some way of productivity to go high.
        This is one of the reasons why we have Play and Akka.
        We were really impressed with the hot reload.
        Now the developers are clearly more productive.
      </>
    ),
  },
  {
    person: 'Jeffrey Olchovy, CTO, Tapad',
    description: (
      <>
        sbt is a core critical component of Tapad's tech stack.
        Its use beyond the build tool is largely responsible for
        the massive efficiency gains that the engineering organization has
        come to appreciate.
        sbt and its interactive shell comprise the interface to the standardized
        application testing, release, and deployment platform.
        Artifacts and applications are delivered, updated, and scaled across
        four global data centers.
      </>
    ),
  },
  {
    person: 'Gabriel Asman, Software Developer, Ovo Energy',
    description: (
      <>
        You can use sbt-native-packager to build native formats like Docker,
        sbt-release has nice steps to take care of versioning,
        sbt-bintray can be used to release to Bintray.
        There's a pattern: You bring in a plugin. It has some settings.
        You read the documentations to learn about the setting.
        And it does its jobs.
      </>
    ),
  },
]

function Testimonial({person, description}) {
  return (
    <div className={clsx('col col--6')}>
      <div className="text--center">
      </div>
      <div className="text--left padding-horiz--md">
        <p>{description}<br/><i>— {person}</i></p>
      </div>
    </div>
  );
}

export default function HomepageTestimonials() {
  return (
    <section className={styles.testimonials}>
      <div className="container">
        <h2>Beyond the build tool</h2>
        <div className="row">
          {TestimonialList.map((props, idx) => (
            <Testimonial key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
