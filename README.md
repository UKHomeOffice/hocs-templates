# hocs-templates

[![CodeQL](https://github.com/UKHomeOffice/hocs-templates/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/UKHomeOffice/hocs-templates/actions/workflows/codeql-analysis.yml)

This is the Home Office Correspondence Service (HOCS) templates service.

## Getting Started

### Prerequisites

* ```Java 17```
* ```Docker```

### Submodules

This project contains a 'ci' submodule with a docker-compose and infrastructure scripts in it.
Most modern IDEs will handle pulling this automatically for you, but if not

```console
$ git submodule update --init --recursive
```

## Running in an IDE

If you are using an IDE, such as IntelliJ, this service can be started by running the ```TemplatesApplication``` main class.
The service can then be accessed at ```http://localhost:8090```.

You need to specify appropriate Spring profiles.
Paste `development` into the "Active profiles" box of your run configuration.

## Versioning

For versioning this project uses [SemVer](https://semver.org/).

## Authors

This project is authored by the Home Office.

## License

This project is licensed under the MIT license. For details please see [License](LICENSE) 
