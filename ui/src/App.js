import React, {Component} from 'react';
import SockJsClient from 'react-stomp';
import './App.css';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import './css/MessageStyle.css';
import NameComponent from "./components/NameComponent";

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            messages: [],
            typedMessage: "",
            name: ""
        }
    }

    setName = (name) => {
        console.log(name);
        this.setState({name: name});
    };

    sendMessage = () => {
        this.clientRef.sendMessage('/queue/sbamqpapp.queue', JSON.stringify({
            name: this.state.name,
            message: this.state.typedMessage,
            notificationType: 'SIMPLE_MESSAGE'
        }));
    };

    displayMessages = () => {
        return (
            <div>
                {this.state.messages.map(msg => {
                    return (
                        <div>
                            {this.state.name == msg.name ?
                                <div>
                                    <p className="title1">{msg.name} : </p><br/>
                                    <p>{msg.message}</p>
                                </div> :
                                <div>
                                    <p className="title2">{msg.name} : </p><br/>
                                    <p>{msg.message}</p>
                                </div>
                            }
                        </div>)
                })}
            </div>
        );
    };

    render() {
        return (
            <div>
                <NameComponent setName={this.setName}/>
                <div className="align-center">
                    <h1>Spring Boot WebSocket with Rabbitmq</h1>
                    <br/><br/>
                </div>
                <div className="align-center">
                    User : <p className="title1"> {this.state.name}</p>
                </div>
                <div className="align-center">
                    <br/><br/>
                    <table>
                        <tbody>
                        <tr>
                            <td>
                                <TextField id="outlined-basic" label="Enter Message to Send" variant="outlined"
                                           onChange={(event) => {
                                               this.setState({typedMessage: event.target.value});
                                           }}/>
                            </td>
                            <td>
                                <Button variant="contained" color="primary"
                                        onClick={this.sendMessage}>Send</Button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <br/><br/>
                <div className="align-center">
                    {this.displayMessages()}
                </div>
                <SockJsClient url='http://localhost:8097/websocket-notifications/'
                              topics={['/queue/sbamqpapp.queue']}
                              onConnect={() => {
                                  console.log("connected to the queue");
                              }}
                              onDisconnect={() => {
                                  console.log("disconnected from the queue");
                              }}
                              onMessage={(msg) => {
                                  var jobs = this.state.messages;
                                  jobs.push(msg);
                                  this.setState({messages: jobs});
                                  console.log(this.state);
                              }}
                              ref={(client) => {
                                  this.clientRef = client
                              }}/>
            </div>
        )
    }
}

export default App;