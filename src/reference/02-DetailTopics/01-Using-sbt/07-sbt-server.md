---
out: sbt-server.html
---

sbt Server
----------

sbt server is a feature that is newly introduced in sbt 1.x, and it's still a work in progress.
You might at first imagine server to be something that runs on remote servers, and does great things, but for now sbt server is not that.

Actually, sbt server just adds network access to sbt's shell command so,
in addition to accepting input from the terminal, server also to accepts input from the network.
This allows multiple clients to connect to a _single session_ of sbt.
The primary use case we have in mind for the client is tooling integration such as editors and IDEs.
As a proof of concept, we created a Visual Studio Code extension called [Scala (sbt)][vscode-sbt-scala].

### Language Server Protocol 3.0

The wire protocol we use is [Language Server Protocol 3.0][lsp] (LSP), which in turn is based on [JSON-RPC][jsonrpc].

The base protocol consists of a header and a content part (comparable to HTTP). The header and content part are separated by a `\r\n`.

Currently the following header fields are supported:

- `Content-Length`: The length of the content part in bytes. If you don't provide this header, we'll read until the end of the line.
- `Content-Type`: Must be set to `application/vscode-jsonrpc; charset=utf-8` or omit it.

Here is an example:

```
Content-Type: application/vscode-jsonrpc; charset=utf-8\r\n
Content-Length: ...\r\n
\r\n
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "textDocument/didSave",
  "params": {
    ...
  }
}
```

A JSON-RPC request consists of an `id` number, a `method` name, and an optional `params` object.
So all LSP requests are pairs of method name and `params` JSON.

An example response to the JSON-RPC request is:

```
Content-Type: application/vscode-jsonrpc; charset=utf-8\r\n
Content-Length: ...\r\n
\r\n
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    ...
  }
}
```

Or the server might return an error response:

```
Content-Type: application/vscode-jsonrpc; charset=utf-8\r\n
Content-Length: ...\r\n
\r\n
{
  "jsonrpc": "2.0",
  "id": 1,
  "error": {
    "code": -32602,
    "message": "some error message"
  }
}
```

In addition to the responses, the server might also send events ("notifications" in LSP terminology).

```
Content-Type: application/vscode-jsonrpc; charset=utf-8\r\n
Content-Length: ...\r\n
\r\n
{
  "jsonrpc": "2.0",
  "method": "textDocument/publishDiagnostics",
  "params": {
    ...
  }
}
```

### Server discovery and authentication

To discover a running server and to prevent unauthorized access to the sbt server, we use a *port file* and a *token file*.

By default, sbt server will be running when a sbt shell session is active. When the server is up, it will create two files called the *port file* and the *token file*. The port file is located at `./project/target/active.json` relative to a build and contains something like:

```json
{
  "uri":"tcp://127.0.0.1:5010",
  "tokenfilePath":"/Users/xxx/.sbt/1.0/server/0845deda85cb41abdb9f/token.json",
  "tokenfileUri":"file:/Users/xxx/.sbt/1.0/server/0845deda85cb41abdb9f/token.json"
}
```

This gives us three pieces of information:

1. That the server is (likely) running.
2. That the server is running on port 5010.
3. The location of the token file.

The location of the token file uses a SHA-1 hash of the build path, so it will not change between the runs.
The token file should contain JSON like the following:

```json
{
  "uri":"tcp://127.0.0.1:5010",
  "token":"12345678901234567890123456789012345678"
}
```

The `uri` field is the same, and the `token` field contains a 128-bits non-negative integer.

### Initialize request

To initiate communication with sbt server, the client (such as a tool like VS Code) must first send an [`initialize` request][lsp_initialize]. This means that the client must send a request with method set to "initialize" and the `InitializeParams` datatype as the `params` field.

To authenticate yourself, you must pass in the token in `initializationOptions` as follows:

```
type InitializationOptionsParams {
  token: String!
}
```

On telnet it would look as follows:

```
\$ telnet 127.0.0.1 5010
Content-Type: application/vscode-jsonrpc; charset=utf-8
Content-Length: 149

{ "jsonrpc": "2.0", "id": 1, "method": "initialize", "params": { "initializationOptions": { "token": "84046191245433876643612047032303751629" } } }
```

After sbt receives the request, it will send an [`initialized` event][lsp_initialized].

### `textDocument/publishDiagnostics` event

The compiler warnings and errors are sent to the client using the `textDocument/publishDiagnostics` event.

- method: `textDocument/publishDiagnostics`
- params: [`PublishDiagnosticsParams`][lsp_publishdiagnosticsparams]

Here's an example output (with JSON-RPC headers omitted):

```
{
  "jsonrpc": "2.0",
  "method": "textDocument/publishDiagnostics",
  "params": {
    "uri": "file:/Users/xxx/work/hellotest/Hello.scala",
    "diagnostics": [
      {
        "range": {
          "start": {
            "line": 2,
            "character": 0
          },
          "end": {
            "line": 2,
            "character": 1
          }
        },
        "severity": 1,
        "source": "sbt",
        "message": "')' expected but '}' found."
      }
    ]
  }
}
```

### `textDocument/didSave` event

As of sbt 1.1.0, sbt will execute the `compile` task upon receiving a `textDocument/didSave` notification.
This behavior is subject to change.

### `sbt/exec` request

A `sbt/exec` request emulates the user typing into the shell.

- method: `sbt/exec`
- params:

```
type SbtExecParams {
  commandLine: String!
}
```

On telnet it would look as follows:

```
Content-Length: 91

{ "jsonrpc": "2.0", "id": 2, "method": "sbt/exec", "params": { "commandLine": "clean" } }
```

Note that there might be other commands running on the build, so in that case the request will be queued up.

### `sbt/setting` request

A `sbt/setting` request can be used to query settings.

- method: `sbt/setting`
- params:

```
type SettingQuery {
  setting: String!
}
```

On telnet it would look as follows:

```
Content-Length: 102

{ "jsonrpc": "2.0", "id": 3, "method": "sbt/setting", "params": { "setting": "root/scalaVersion" } }
Content-Length: 87
Content-Type: application/vscode-jsonrpc; charset=utf-8

{"jsonrpc":"2.0","id":"3","result":{"value":"2.12.2","contentType":"java.lang.String"}}
```

Unlike the command execution, this will respond immediately.

  [lsp]: https://github.com/Microsoft/language-server-protocol/blob/master/protocol.md
  [jsonrpc]: http://www.jsonrpc.org/specification
  [vscode-sbt-scala]: https://marketplace.visualstudio.com/items?itemName=lightbend.vscode-sbt-scala
  [lsp_initialize]: https://github.com/Microsoft/language-server-protocol/blob/master/protocol.md#initialize
  [lsp_initialized]: https://github.com/Microsoft/language-server-protocol/blob/master/protocol.md#initialized
  [lsp_publishdiagnosticsparams]: https://github.com/Microsoft/language-server-protocol/blob/master/protocol.md#publishdiagnostics-notification
