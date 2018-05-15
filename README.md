# Popular Movies
An app for displaying a list of popular movies according
to[The Movie Database (TMDb).](https://www.themoviedb.org/)

To use this app you will need to add an API key for TMDb.
If you do not already have one,
first[create an account,](https://www.themoviedb.org/account/signup)
then follow the
instructions[here.](https://developers.themoviedb.org/3/getting-started/introduction)

Once you have an API key, create a file called `tmdb.properties`
in the `app/src/main/assets` directory and add the following
property definition:

`api_key =` _your_api_key_

where _your_api_key_ should be replaced with your actual
API key from TMDb.

Alternatively, you can simply assign a value of
_your_api_key_ to the `API_KEY` variable in the 
`NetworkUtils` class.
