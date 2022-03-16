# A Text Based Adventure Engine

This idea was originated in a hackathon - the concept was a silly take on text based adventures where the fun bit was literally using sms. The goal here is to parse a twilio message body, updating game state for the given number, and responding to the requested action. The engine will rely on structured items, locations, characters, and events. A thing I'd like to eventually make is a way to generate a game using via a site.

## Endpoints

* `/api/v1/sms`

## Key Concepts

### Supported Actions

### Items

### Locations

### Characters 

### Events

## Testing

## Local Development

```lein ring server```

## License

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
