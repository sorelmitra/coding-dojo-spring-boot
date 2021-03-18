import { sleep } from 'k6';
import http from 'k6/http';

import { checks, getCities, defaultOrEnv } from './utils.js';

const cityList = JSON.parse(open('./city.list.json'));

export function setup() {
	let data = {
		server: "localhost",
		port: 8080,
		path: "weather",
		cityNames: null,
		countVus: parseInt(defaultOrEnv(0, "K6_VUS")),
		index: 0,
	};
	if (data.countVus == 0) {
		console.log("ERROR: Please specify the exact number of VUs in the 'K6_VUS' variable!");
		return data;
	}
	data.cityNames = getCities(cityList, "US");
	//console.log(data.cityNames);
	console.log(`Endpoint ${data.server}:${data.port}/${data.path}, available cities count ${data.cityNames.length}`);
	return data;
}

export default function weather(data) {
	if (__VU > data.countVus) {
		console.log(`VU ${__VU} > total VUs ${data.countVus}, doing nothing`);
		return;
	}

	if (data.index == 0) {
		for (let i = 0; i < __VU - 1; i++) {
			data.index++;
		}
	}
	let city = data.cityNames[data.index];
	//console.log(`VU ${__VU}, index ${data.index}, city ${city}`);

	let url = `http://${data.server}:${data.port}/${data.path}?city=${encodeURIComponent(city)}&fake=true`;
	let expectedStatus = 200;
	let r = http.post(url);
	if (r.status != expectedStatus) {
		console.log(r.body);
	}
	checks.status("weather", r, expectedStatus);
	let d = JSON.parse(r.body);
	checks.value("weather", "city", city, d.city);

	data.index = data.index + data.countVus;
}

export function teardown(data) {
}
