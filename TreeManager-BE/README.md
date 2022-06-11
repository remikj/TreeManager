# Backend for TreeManager

## Usage

### Prerequisites for running application

- Network access may be necessary for downloading dependencies
- Port 8080 available
- JDK 11

### Running the application

To run application execute command from TreeManager-BE directory:

```bash
make run
```

or (if maven is installed)

```bash
mvn spring-boot:run
```

### Data

Application is used to manage data in following format:

```json
{
  "id": 1,
  "value": 5,
  "sumToRoot": 5,
  "children": []
}
```

where:

- id is a tree node identifier
- value is a number held by root and used for calculating sumToRoot
- sumToRoot is a sum of all values to root tree node
- children is a list of all child tree nodes

### Using the API

#### Swagger documentation

Swagger documentation and access to the API is provided after running the application at:
http://localhost:8080/swagger-ui/index.html#/

#### */tree* endpoint

Endpoint is responsible for managing the whole tree and allows getting, resetting and overriding data

##### GET

Returns whole tree

```bash
curl --request GET \
  --url http://localhost:8080/tree
```

Sample response

```json
{
  "id": 1,
  "value": 0,
  "sumToRoot": 0,
  "children": [
    {
      "id": 2,
      "value": 14,
      "sumToRoot": 14,
      "children": [
        {
          "id": 5,
          "value": 25,
          "sumToRoot": 39,
          "children": []
        }
      ]
    },
    {
      "id": 3,
      "value": 12,
      "sumToRoot": 12,
      "children": []
    },
    {
      "id": 4,
      "value": -92,
      "sumToRoot": -92,
      "children": []
    }
  ]
}
```

##### PUT

Overrides tree

```bash
curl --request PUT \
  --url http://localhost:8080/tree \
  --header 'Content-Type: application/json' \
  --data '<EXAMPLE JSON BELOW>'
```

Example json that can be used to override the tree

```json
{
  "value": 4,
  "children": [
    {
      "value": 3,
      "children": [
        {
          "value": 2
        }
      ]
    },
    {
      "value": 19,
      "children": [
        {
          "value": 2,
          "children": [
            {
              "value": 3
            }
          ]
        },
        {
          "value": 17
        },
        {
          "value": 1
        }
      ]
    }
  ]
}
```

#### DELETE

Resets tree (old tree is deleted and new root is created with zero value)

```bash
curl --request DELETE \
  --url http://localhost:8080/tree
```

Tree after reset:

```json
{
  "id": 6,
  "value": 0,
  "sumToRoot": 0,
  "children": []
}
```

### */tree/nodes* endpoint

Endpoint to manage tree nodes by their id.

#### GET /tree/nodes/{nodeId}

Returns node with given nodeId

```bash
curl --request GET \
  --url 'http://localhost:8080/tree/nodes/8'
```

Example response

```json
{
  "id": 8,
  "value": 3,
  "sumToRoot": 7,
  "children": [
    {
      "id": 9,
      "value": 2,
      "sumToRoot": 9,
      "children": []
    }
  ]
}
```

#### PATCH /tree/nodes/{nodeId}

Update value of node with given nodeId
SumToRoots are recalculated for the node and all its children

```bash
curl --request PATCH \
  --url http://localhost:8080/tree/nodes/8 \
  --header 'Content-Type: application/json' \
  --data '{"value": 17}'
```

#### DELETE /tree/nodes/{nodeId}

Delete node with given nodeId and all its children

```bash
curl --request DELETE \
  --url http://localhost:8080/tree/nodes/8
```

#### POST /tree/nodes/{nodeId}/addChild

Creates a new child node for a node with given nodeId

```bash
curl --request POST \
  --url http://localhost:8080/tree/nodes/8/addChild \
  --header 'Content-Type: application/json' \
  --data '{"value": 25}'
```

#### POST /tree/nodes/{nodeId}/addChildWithChildren

Creates a new child node with optional children for a node with given nodeId

```bash
curl --request POST \
  --url http://localhost:8080/tree/nodes/8/addChildWithChildren \
  --header 'Content-Type: application/json' \
  --data '{
	"value": 25,
	"children": [{"value": 15},{"value": -13}]
}'
```

#### POST /tree/nodes/{nodeId}/copyTo/{targetParentNodeId}

Copy node with given nodeId as a child of node with targetParentNodeId

```bash
curl --request POST \
  --url http://localhost:8080/tree/nodes/1/copyTo/11
```

#### POST /tree/nodes/{nodeId}/moveTo/{targetParentNodeId}

Move node with given nodeId as a child of node with targetParentNodeId
Moving node to one of its children nodes is not allowed

```bash
curl --request POST \
  --url http://localhost:8080/tree/nodes/1/moveTo/1
```

## Development

### Building project with tests

```bash
make build-all
```

### Building project without tests

```bash
make build
```

## Next steps

- Dockerization
- Separating DB (currently in memory DB is used)
- Improvement of maven stages (adding profiles for cases)
- Improvement of logging (adding logs for easier debugging)
- Automatic testing
- Extending integration tests with more fail cases