# OVOroo API

## Create an order
POST /orders
```{
    "owner": { // optional
        "name": "Daniela", 
        "email": "daniela.sfregola@gmail.com" 
    },
    "location": "London",
    "capacity": 6, // by default, it is 4
    "duration": 15, // by default, it is 10
    "products": [{
        "name": "Espresso",
        "requester": { 
                "name": "Daniela", 
                "email": "daniela.sfregola@gmail.com" 
        },
        "size": {
           "name":"Regular",
           "price": 2.90
        },
        "extras": [{
                    "name":"Decaf",
                    "price": 0.10
                 }]
    }]
}```


## Get active order for location
GET /orders?location="London"

## Get order by id
GET /orders/ABC123

## Assign order
PUT /orders/ABC123/owner
```
{ 
    "name": "Daniela", 
    "email": "daniela.sfregola@gmail.com" 
}
```

## Add to order
PUT /orders/ABC123/items
```
{
        "name": "Espresso",
        "requester": { 
                "name": "Yo Bro", 
                "email": "yo.bro@gmail.com" 
        },
        "size": {
           "name":"Regular",
           "price": 2.90
        },
        "extras": [{
                    "name":"Decaf",
                    "price": 0.10
                 }]
}
```
