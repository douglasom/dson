Transforms a json object into a single row in a csv.

A json object might have nested objects and/or list of values. If you want a json object to be transormed into a single row in a CSV, you might have just got lucky finding this lib you're reading about right now.

Example:

{
    "name": "Douglas",
    "hobbies": [
        "motorcycling",
        "videogames",
        "music"
    ],
    "contactInfo": {
        "email": "douglas.mendes@gmail.com",
        "github": "douglasom"
    }
}

When transormed into CSV:

contactInfo.email,contactInfo.github,hobbies,name\n
douglas.mendes@gmail.com,douglasom,"motorcycling;videogames;music",Douglas\n
