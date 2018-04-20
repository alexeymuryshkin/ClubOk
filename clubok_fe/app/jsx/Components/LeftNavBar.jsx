import React, {Component} from 'react';
import {Card, Comment, Form, Header, Icon, Image, Input} from 'semantic-ui-react';

import '../../less/feed.less';
import ServerRequest from "../ServerRequest";

class LeftNavBar extends Component {
    constructor(props) {
        super(props);
        //this.state = {}
        this.onSearchChange = this.onSearchChange.bind(this);
        this.openMainPage = this.openMainPage.bind(this);
    }

    onSearchChange(e) {
        let text = e.target.value;
        console.log(text);
    }

    openMainPage() {
        console.log("Opening of The Main Page");
        window.location.replace("http://localhost:3000/");
    }

    signOut() {
        ServerRequest.getInstance().signOut();
    }

    render() {
        return (
            <Card fluid='true'>
                <Card.Content>
                    <Header>
                        <Image src="logo/apple.jpg"/>
                        <Card.Content>
                            <a href="#">Apple</a>
                            <Header.Subheader>today, 20:13</Header.Subheader>
                        </Card.Content>
                    </Header>
                </Card.Content>
                <Card.Content>
                    <p>
                        Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been
                        the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley
                        of type and scrambled it to make a type specimen book. It has survived not only five centuries,
                        but also the leap into electronic typesetting, remaining essentially unchanged. It was
                        popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                        and more recently with desktop publishing software like Aldus PageMaker including versions of
                        Lorem Ipsum.
                    </p>
                    <Image fluid='true' src='images/paris.jpg'/>
                </Card.Content>
                <Card.Content>
                    <span className="right floated">
                        <Icon name='heart outline like'/>
                        17 likes
                    </span>
                    <Icon name='comment'/>
                    3 comments
                </Card.Content>
                <Card.Content>
                    <Comment.Group>
                        <Comment>
                            {/*<Comment.Avatar src='/logo/apple.jpg' />*/}
                            <Comment.Content>
                                <Comment.Author as='a'>Matt</Comment.Author>
                                <Comment.Metadata>
                                    <div>Today at 5:42PM</div>
                                </Comment.Metadata>
                                <Comment.Text>How artistic!</Comment.Text>
                                <Comment.Actions>
                                    <Comment.Action>Reply</Comment.Action>
                                </Comment.Actions>
                            </Comment.Content>
                        </Comment>
                    </Comment.Group>
                </Card.Content>
                <Card.Content>
                    <Form reply='true'>
                        <Input transparent='true' icon='pencil alternate' iconPosition='left' fluid='true' placeholder='Enter your comment...'/>
                    </Form>
                </Card.Content>
            </Card>
        );
    }
}

export default LeftNavBar;