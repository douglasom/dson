Transforms a json object into a single row in a csv.

A json object might have nested objects and/or list of values. If you want a json object to be transformed into a single row in a CSV, you might have just gotten lucky finding this lib you're reading about right now.

Example:
```
{
    "name": "Joe",
    "hobbies": [
        "motorcycling",
        "videogames",
        "music"
    ],
    "contactInfo": {
        "email": "joedoe@nowhere.com",
        "github": "joedoe"
    }
}
```
When transformed into CSV:
```
contactInfo.email,contactInfo.github,hobbies,name\n
joedoe@nowhere.com,joedoe,"motorcycling;videogames;music",Joe\n
```
And like this when seen as a table:

|contactInfo.email |contactInfo.github|hobbies                        |name   |
|------------------|------------------|-------------------------------|-------|
|joedoe@nowhere.com|joedoe            |"motorcycling;videogames;music"|Joe    |

Notice the alphabetical order instead of JSON fields order? That might bother you but consider this: field order isn't something predicted by the JSON representation convention. So it's not a good idea do rely on that anyway. We might eventually add a Comparator as an optional parameter, though.
