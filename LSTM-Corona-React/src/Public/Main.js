import React from 'react';
import axios from "axios";
import { Box, Container, Divider, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography, Paper, IconButton, FormControl, Select, MenuItem, CircularProgress, LinearProgress, InputLabel, Accordion, AccordionSummary, AccordionDetails } from '@material-ui/core';
import Copyright from '../Component/Copyright'
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend, Label } from 'recharts';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

export default class Main extends React.Component {

    constructor() {
        super();
        this.state = {
            country: "",
            progress: false,
            data: {
                learnData: [
                    {
                        date: "",
                        active: 0
                    }
                ],
                testData: [
                    {
                        date: "",
                        active: 0
                    }
                ],
                trainData: [
                    {
                        date: "",
                        active: 0
                    }
                ],
            },
            countries: [
                {
                    slug: "",
                    countryCode: "",
                    country: "",
                }
            ],
        }
    }

    componentDidMount() {
        this.getSummary();
    }

    getSummary() {
        var config = {
            method: 'get',
            url: 'http://localhost:8080/Api/GetAllCountry',
            headers: {
                'Content-Type': 'application/json'
            }
        };

        axios(config)
            .then(response => {
                this.setState({ countries: response.data })
                console.log(this.state.countries);
            })
            .catch(error => {
                console.log(error);
            });
    }

    getData() {
        var config = {
            method: 'get',
            url: 'http://localhost:8080/Api/GetCountrySummary/' + this.state.country,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        axios(config)
            .then(response => {
                response.data.trainData.map((active) => (
                    active.date = new Date(active.date).toLocaleDateString()
                ));
                response.data.learnData.map((active) => (
                    active.date = new Date(active.date).toLocaleDateString()
                ));
                response.data.testData.map((active) => (
                    active.date = new Date(active.date).toLocaleDateString()
                ));
                this.setState({ data: response.data })
                this.setState({ progress: false })
            })
            .catch(error => {
                console.log(error);
                this.setState({ progress: false })
            });
    }

    handleCategoryChanged(event) {
        if (event.target.value != "") {
            this.state.country = event.target.value;

            this.setState({ country: event.target.value })
            console.log(this.state.country);
            this.getData();
            this.setState({ progress: true })
        }

    };

    LineChart
    render() {
        return (
            <Box style={{ marginBottom: "60px" }}>
                <Box style={{ backgroundColor: "#0039B4", padding: "10px", color: "white" }}>
                    <Typography align="center" variant="h4">COVID-19 CORONAVIRUS PANDEMIC - LSTM</Typography>
                </Box>
                <Divider />
                <Container maxWidth="lg">
                    <Box style={{ padding: "10px" }} align="center" >
                    <Typography align="center" variant="h6" style={{ marginBottom: "10px",marginLeft: "10px" }}>
                                Choose Country
                            </Typography>
                        <FormControl variant="outlined" fullWidth size="small" align="left" style={{ maxWidth: "500px" }}>
                            <InputLabel id="demo-simple-select-outlined-label">Country</InputLabel>
                            <Select
                                labelId="demo-simple-select-outlined-label"
                                id="demo-simple-select-outlined"
                                value={this.state.country}
                                onChange={this.handleCategoryChanged.bind(this)}
                                label="Country"
                            >
                                <MenuItem key="" value="">
                                    <em>None</em>
                                </MenuItem>
                                {this.state.countries.map((country) => (
                                    <MenuItem key={country.slug} value={country.slug}>{country.country}</MenuItem>
                                ))}
                            </Select>

                            {this.state.progress ? <LinearProgress style={{ marginTop: "10px" }} /> : null}

                        </FormControl>

                    </Box>
                    <Divider></Divider>

                    <Box style={{ padding: "10px" }} >
                        <Box align="center" >
                            <Typography align="center" variant="h6" style={{ marginBottom: "10px",marginLeft: "10px" }}>
                                Result Graph
                            </Typography>
                            <ResponsiveContainer width={'100%'} height={500}>

                                <AreaChart
                                    margin={{
                                        top: 16,
                                        right: 50,
                                        bottom: 0,
                                        left: 16,
                                    }}
                                >
                                    <CartesianGrid strokeDasharray="4 4" />
                                    <XAxis dataKey="date" type="category" allowDuplicatedCategory={false}>
                                    </XAxis>
                                    <YAxis dataKey="active">
                                        <Label
                                            angle={270}
                                            position="left"
                                            style={{ textAnchor: 'middle' }}
                                        >
                                            Active Case
                                     </Label>
                                    </YAxis>
                                    <Tooltip />
                                    <Legend />
                                    <Area dataKey="active" data={this.state.data.trainData} name="LSTM Train" stroke="#00FF17" key="1" fill="#00FF17" activeDot={{ r: 0 }} />
                                    <Area dataKey="active" data={this.state.data.testData} name="LSTM Test" stroke="#0051FF" key="3" fill="#0051FF" activeDot={{ r: 0 }} />
                                    <Area dataKey="active" data={this.state.data.learnData} name="LSTM Learn" stroke="#FF0000" key="2" fill="#FF0000" activeDot={{ r: 0 }} />
                                </AreaChart>
                            </ResponsiveContainer>
                        </Box>

                    </Box>
                    <Divider></Divider>
                    <Box style={{ padding: "10px" }}>
                        <Typography align="center" variant="h6" style={{ marginBottom: "10px" , marginLeft: "10px" }}>
                            Result Table
                        </Typography>
                        <Accordion>
                            <AccordionSummary
                                expandIcon={<ExpandMoreIcon />}
                                aria-controls="panel1a-content"
                                id="panel1a-header"
                            >
                                <Typography >Learn Data Table</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                <TableContainer component={Paper}>
                                    <Table aria-label="simple table">
                                        <TableHead>
                                            <TableRow>
                                                <TableCell align="center">#</TableCell>
                                                <TableCell align="center">Date</TableCell>
                                                <TableCell align="center">Active Case</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {this.state.data.learnData.map((row, index) => (
                                                <TableRow key={index + "a"}>
                                                    <TableCell align="center">{index + 1}</TableCell>
                                                    <TableCell align="center">{row.date}</TableCell>
                                                    <TableCell align="center">{row.active}</TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </AccordionDetails>
                        </Accordion>
                        <Accordion>
                            <AccordionSummary
                                expandIcon={<ExpandMoreIcon />}
                                aria-controls="panel1a-content"
                                id="panel1a-header"
                            >
                                <Typography >Test Data Table</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                <TableContainer component={Paper}>
                                    <Table aria-label="simple table">
                                        <TableHead>
                                            <TableRow>
                                                <TableCell align="center">#</TableCell>
                                                <TableCell align="center">Date</TableCell>
                                                <TableCell align="center">Active Case</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {this.state.data.testData.map((row, index) => (
                                                <TableRow key={index + "b"}>
                                                    <TableCell align="center">{index + 1}</TableCell>
                                                    <TableCell align="center">{row.date}</TableCell>
                                                    <TableCell align="center">{row.active}</TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </AccordionDetails>
                        </Accordion>
                        <Accordion>
                            <AccordionSummary
                                expandIcon={<ExpandMoreIcon />}
                                aria-controls="panel1a-content"
                                id="panel1a-header"
                            >
                                <Typography >Train Data Table</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                <TableContainer component={Paper}>
                                    <Table aria-label="simple table">
                                        <TableHead>
                                            <TableRow>
                                                <TableCell align="center">#</TableCell>
                                                <TableCell align="center">Date</TableCell>
                                                <TableCell align="center">Active Case</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {this.state.data.trainData.map((row, index) => (
                                                <TableRow key={index + "c"}>
                                                    <TableCell align="center">{index + 1}</TableCell>
                                                    <TableCell align="center">{row.date}</TableCell>
                                                    <TableCell align="center">{row.active}</TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </AccordionDetails>
                        </Accordion>
                    </Box>

                </Container>
                <Copyright></Copyright>
            </Box>

        );
    }
}