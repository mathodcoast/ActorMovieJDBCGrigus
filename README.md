# ActorMovieJDBCGrigus
Bobocode exercise project.
<p><strong>Implement a persistence layer using JDBC API for a simple Actor-Movie application.</strong></p>
<p>&nbsp;</p>
<ol>
<li style="font-weight: 400;"><span style="font-weight: 400;">Create a new empty maven project</span></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">Add all required dependencies </span></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">Create class </span><span style="font-weight: 400;">Actor</span> <em><span style="font-weight: 400;">(id, first name, last name, birthday)</span></em></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">Create class </span><span style="font-weight: 400;">Movie</span> <em><span style="font-weight: 400;">(id, name, duration, release date)</span></em></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">Design a database, and implement a corresponding SQL script to create all required tables. Please note that database should be able to</span><strong> store the many to many relation between actors and movies</strong></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">Create </span><span style="font-weight: 400;">ActorDao</span><span style="font-weight: 400;"> and implement methods that allow to:</span></li>
<ol>
<li style="font-weight: 400;"><span style="font-weight: 400;">save new actor</span></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">find actor by id</span></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">link actor to some movie by actor and movie ids</span></li>
</ol>
<li style="font-weight: 400;"><span style="font-weight: 400;">Create </span><span style="font-weight: 400;">MovieDao</span><span style="font-weight: 400;"> and implement methods that allow to:</span></li>
<ol>
<li style="font-weight: 400;"><span style="font-weight: 400;">save new movie</span></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">find all movies</span></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">search movie by name</span></li>
<li style="font-weight: 400;"><span style="font-weight: 400;">search movies by actor first and last name</span></li>
</ol>
<li style="font-weight: 400;"><span style="font-weight: 400;">Push your project to github</span></li>
</ol>
