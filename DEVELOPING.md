Globalization
-------------

To add a new locale (es, zh-cn, etc):

```bash
script/genpo.sh xx
```

To resync the po files to the original:

```bash
script/sync.sh xx
```

To serve the po files, run the following:

```bash
script/serve.sh xx
```

When you changed a `*.po` file, run:

```bash
script/concat.sh xx
```


Diataxis
--------

The following is the [four kinds of documentation](https://www.writethedocs.org/videos/eu/2017/the-four-kinds-of-documentation-and-why-you-need-to-understand-what-they-are-daniele-procida/) structure, which I'm not really sure would work, but worth giving it some try.

### tutorial

> Tutorials are lessons given by a teacher that take the reader by the hand through series of steps to complete a task.
> Your project needs to show the user that they achieve something with it. The tutorials are oriented towards user's learning. You get them to do specific things, and allow them to learn later. In tutorials, you don't explain anything they need to know now.

"sbt by example" is our tutorial.

### how-to guides

> How-to guides take the reader through the steps required to solve specific, real-world problem. They are, recipes, directions to achieve specific ends. They are goal-oriented. They are completely distinct from tutorials. A how-to guide is an answer to a question. In tutorial, you are the teacher: In how-to guide, the user knows what the problem is that they are trying to solve, and you've got the answer for them. How-to guide shouldn't explain everything, because the explanation get in the way of action.

### concept guides

> Explanations are discussions that illuminate or clarify a particular topic. They are oriented towards understanding. They are chances to step back from the machinery, and take a wider view. It provides historical, sociological background discussion.

'The Book of sbt' calls the Explanations "Concepts."

### reference guides

> Reference guides are technical descriptions of machinery and its operation. They have only one job, which is to describe. They are information-oriented. It's not a place for explaining basic concept.

'The Book of sbt' calls this Reference.

