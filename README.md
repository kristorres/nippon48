Nippon48
========

**Nippon48** is an interactive web application that lets you add/delete Nippon48
members in the database, and also update their stats. The application is written
in Scala, using the Play Framework. Data storage is done using CouchDB.

But what is a Nippon48 member, you may ask? A **Nippon48 member** is a member of
the Japanese idol girl group *AKB48* (est. 2005 from Akihabara, Tokyo), one of
its domestic sister groups, or one of its rival groups. AKB48 consists of five
teams: Team A, Team B, Team K, Team 4, and Team 8.

#### Domestic Sister Groups

  * *SKE48* (est. 2008 from Sakae, Nagoya) consists of three teams:
    Team S, Team K, and Team E.
  * *NMB48* (est. 2010 from Namba, Osaka) consists of three teams:
    Team N, Team M, and Team BII.
  * *HKT48* (est. 2011 from Hakata, Fukuoka) consists of three teams:
    Team H, Team KIV, and Team TII.
  * *NGT48* (est. 2015 from Niigata) consists of one team: Team NIII.

#### Rival Groups

  * *Nogizaka46* (est. 2010 from Tokyo)
  * *Keyakizaka46* (est. 2015 from Tokyo)

All idol girl groups have “teams” dedicated for their *kenkyuusei* (trainees,
lit. “research students” in Japanese). There is also a Team Unknown, a temporary
team created for Nippon48 members that are promoted or transferred, but not to a
specific team. Finally, members may have a concurrent position in two different
groups and teams at the same time.

The concept of a captain is used to describe a Nippon48 member that is the
“authority” of a certain team. There is also the concept of a co-captain, but
for the sake of simplicity, a co-captain is also considered a captain.

AKB48 and its domestic sister groups have their own theater managers. Currently,
Rino Sashihara is the only Nippon48 member to have a concurrent position as a
theater manager (of HKT48).

Requirements
------------

Before you begin, make sure you have downloaded the
[Lightbend Activator](https://www.lightbend.com/activator/download), extracted
its *.zip* file, and moved it to a preferable directory (e.g., */path/to/play*).
In order to have the `activator` command available in Terminal, add the
following line in the hidden *.bash_profile* file, located in your home
directory:

```
export PATH=$PATH:/path/to/play
```

Also, if SBT has not yet been installed, then run the following command in
Terminal:

```
brew install sbt
```

Finally, make sure you configure the Cloudant database that contains the
Nippon48 members in the *nippon48-ws/conf/application.conf* file of this
project. The database must also have the following design document:

```json
{
  "_id": "_design/members",
  "views": {
    "all": {
      "map": "function(doc) { emit(doc._id, [doc.name_en, doc.name_jp, doc.birthdate, doc.groups, doc.teams, doc.captain]); }"
    }
  }
}
```

Installation
------------

Clone the repository into a local directory. Then run the following command in
Terminal:

```
activator dist
```

This will produce a *.zip* file containing all the *.jar* files needed to
compile and run the Nippon48 application in the *target/universal* folder of the
project. Now you can enjoy running the application per the instructions for
[Play Framework](https://www.playframework.com).