Overview
========

Lazy loading 
------------

The main building blocks (Document, ServerResponse, ViewResult) contain the json string.
The toString() method returns the JSON representation.

Whenever another method is called the a lazy loaded instance of the type will
be populated and used.