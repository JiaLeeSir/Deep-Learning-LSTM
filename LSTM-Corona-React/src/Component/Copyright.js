import React from 'react';
import Typography from '@material-ui/core/Typography';
import Link from '@material-ui/core/Link';
import { Box, Divider } from '@material-ui/core';

export default function Copyright() {

  return (
      <Box style={{backgroundColor:"#0039B4",padding: "10px", color: "white",position: "fixed",width: "100%",left: "0",
    bottom: "0"}}>
      <Typography variant="body2" variant="h6" align="center" >
        <Link color="inherit" href="/">
          LSTM Project
        </Link>{' '}
        {new Date().getFullYear()}
      </Typography>
    </Box>

  );
}