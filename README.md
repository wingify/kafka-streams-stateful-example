# kafka-streams-stateful-example

This example demonstrates aggregation in Kafka Streams with two different approaches, one based on DSL operators like `groupByKey` and `reduce`,
and another using Kafka Streams Processor API and state stores.

## Dependencies

- **JAVA 8**
- **Maven** and **Kafka** should be installed for running this project.

## How To Run

- Create input Kafka topic `user-activity` from Kafka root directory:\

    ``` bash
    bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic user-activity
    ```

- Run `mvn clean compile assembly:single`. A jar named `kafka-streams-stateful-1.0-SNAPSHOT-jar-with-dependencies.jar` will be created in `target` directory.

- A data generation script is present in the `TestDataGenerator` class with which you can push Kafka messages to the input Kafka topic:\

    ```bash
    java -cp target/kafka-streams-stateful-1.0-SNAPSHOT-jar-with-dependencies.jar TestDataGenerator {{number_of_messages}}
    ```

- To Run the Kafka Streams Application:\

    ```bash
    java -cp target/kafka-streams-stateful-1.0-SNAPSHOT-jar-with-dependencies.jar Main
    ```

## Diagrams

* **Kafka Streams Topology**

    <img src="diagrams/kafka-streams-topology.png" alt="kafka-streams-topology">

* **Kafka Streams Flow Diagram**

    <img src="diagrams/kafka-streams-flow-diagram.png" alt="kafka-streams-flow-diagram">

## Author

Aditya Gaur [@aditya232](https://github.com/aditya232)

## Contributing

Please go through our [contributing guidelines](https://github.com/wingify/kafka-streams-stateful-example/blob/master/CONTRIBUTING.md)

## Code of Conduct

[Code of Conduct](https://github.com/wingify/kafka-streams-stateful-example/blob/master/CODE_OF_CONDUCT.md)

## Copyright and License

> The MIT License (MIT)
>
> Copyright (c) 2020 Wingify Software Pvt. Ltd.
>
> Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
