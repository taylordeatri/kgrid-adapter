import http from "k6/http";
import { check, fail } from "k6";

const params = {headers: {'Content-Type': 'application/json'}};
let hostname = __ENV.HOSTNAME === undefined ? 'localhost:8082' : __ENV.HOSTNAME
let testObjects = [
  {endpoint: "99999/python/v0.0.1/hello", input: {"name": "Ted"}, name:"Ted", greeting: "hello"},
  {endpoint: "99999/python2/v0.0.1/hello", input: {"name": "Will"}, name:"Will", greeting: "goodbye"},
  {endpoint: "99999/python3/v0.0.1/hello", input: {"name": "Peter"}, name:"Peter", greeting: "apple"},
  {endpoint: "99999/python4/v0.0.1/hello", input: {"name": "Fred"}, name:"Fred", greeting: "bagel"},
  {endpoint: "99999/python5/v0.0.1/hello", input: {"name": "Sam"}, name:"Sam", greeting: "carrot"},
  {endpoint: "99999/python6/v0.0.1/hello", input: {"name": "Alice"}, name:"Alice", greeting: "donut"},
  {endpoint: "99999/python7/v0.0.1/hello", input: {"name": "Sarah"}, name:"Sarah", greeting: "ham"},
  {endpoint: "99999/python8/v0.0.1/hello", input: {"name": "Tina"}, name:"Tina", greeting: "icee"},
  {endpoint: "99999/python9/v0.0.1/hello", input: {"name": "Patrice"}, name:"Patrice", greeting: "jam"},
  {endpoint: "99999/python10/v0.0.1/hello", input: {"name": "Beth"}, name:"Beth", greeting: "kale"}

]

export default function() {
  //Get test data
  let randomtestObject = testObjects[Math.floor(Math.random() * testObjects.length)];

  //construct URL
  const url = `http://${hostname}/`+randomtestObject.endpoint;
  //Call endpoint with input data
  let response = http.post(url,JSON.stringify(randomtestObject.input), params);

  //Check Response
  check(response, {
    'is status 200': (r) => r.status === 200,
    "name" : (r) => JSON.parse(r.body).result.indexOf(randomtestObject.name) !== -1,
    "greeting": (r) => JSON.parse(r.body).result.indexOf(randomtestObject.greeting) !== -1,
  }) || fail(response.body);

};