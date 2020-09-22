import React from 'react';
import ReactDOM from 'react-dom';
import Main from './Public/Main';
import { BrowserRouter as Router, Route } from 'react-router-dom';



ReactDOM.render(
    <Router>
        <Route exact path="/" component={Main}/>
    </Router>,document.getElementById('root')
);

