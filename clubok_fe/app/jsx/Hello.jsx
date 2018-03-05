import React, {Component} from 'react'

import '../less/Hello.less'

class Hello extends Component{
    render() {
        return (
            <div className="HelloWorld">Hello {this.props.who}</div>
        )
    }
}

export default Hello
