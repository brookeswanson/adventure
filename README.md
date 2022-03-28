# A Text Based Adventure Engine

This idea was originated in a hackathon - the concept was a silly take on text based adventures where the fun bit was literally using sms. The goal here is to parse a twilio message body, updating game state for the given number, and responding to the requested action. The engine will rely on a structured story map with actions like use and take relying on deep merging map values. A thing I'd like to eventually make is a way to generate a game using a website.

This app is auto deployed using heroku and using github to run tests. In order to set this up you need to:

1. Create a new heroku project
2. Add a DB connection
3. Add a new DB_URL env var to the app (Heroku's default DATABASE_URL can't be parsed easily by ragtime or next.jdbc)
4. Set up a twilio account, and secure a number
5. point the sms webhook to the deployed app

## Deployed Application

https://adventures-in-texting.herokuapp.com/

Interact by texting [(608)-471-6866](sms:16084716866)

## Endpoints

* POST `/api/v1/sms`, expects two query parameters: From and Body
* GET `/`, html page

## Namespaces

* `core` - main app entrypoint, where the service lives
* `engine` - parses out a request string into a command map the game can work with
* `twilio` - generates a TwiML payload from a response string
* `db.migrations` - runs migrations on startup
* `db.game` - handles upsert, delete, and getting game instances
* `game.core` - dispatches on command value and returns an updated game with a response
* `game.message` - holds default messages for error states etc
* `game.story` - contains the default game map as well as helper functions to deal with handling game state

## Key Concepts

### Supported Actions

* use - use an item in your inventory
* look - Get descriptions for the room etc
* take - Pick up an item
* guide - "Help" functionality, help means something special to twilio so this is a way to get through that
* inventory - see items you've picked up so far
* meow - do it for the bit
* restart - reset your game state

### Items

Nested under `:interactions` items are represented by a collection of applicable commands

```clojure
{:key {:look "look at the key"}}
```
### Locations

The keys are the name of the location and are nested directly under the `:maps` key.

```clojure
{:maps
 {:location1 {}
  :location2 {}}}
```

The main things in a location are interactions and initial description.

```clojure
{:initial "You walk into the room"
 :interactions {:key {:look "It's a key"}}}
 ```

 Note that under `interactions` there should be a default key to provide backup values for when things are tried in certain rooms.

## Testing

run tests by running

```lein test```

## Local Development

Copy the env file and update the DB_URL to have a jdbc connection string
```cp env.example .env```

Then source the env file

```source .env```

Start the DB

```docker-compose up -d```

Start the server

```lein ring server```

Go to http://localhost:8080 to see the homepage

or test the sms endpoint

```http POST localhost:8080/api/v1/sms From==woof Body=="look around"```