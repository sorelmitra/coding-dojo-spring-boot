import { check } from 'k6';

export let checks = {

	status: function checkStatus(name, response, expectedStatus) {
		let checks = {};
		checks[`${name} / response status is ${expectedStatus}`] = 
			r => expectedStatus == r.status;
		check(response, checks);
	},

	value: function checkValue(name, fieldName, expected, toCheck) {
		let checks = {};
		if (no(toCheck) || toCheck == "") {
			if (no(expected) || expected == "") {
				toCheck = "";
				expected = "";
			}
		}
		checks[`${name} / value of field ${fieldName}`] = 
			value => {
				if (expected == value) return true;
				console.log(`WARNING: Check for field ${name}/${__ITER}_${__VU}/'${fieldName}' failed:\nExpected <${expected}>\n     Got <${value}>\nEnd of WARNING`);
				return false;
			};
		check(toCheck, checks);
	},
};

export function getCities(cityList, countryCode) {
	let cities = [];
	cityList.forEach(city => {
		if (city.country == countryCode) {
		    let entry = {
		        name: city.name,
		        cityId: city.id,
		    };
			cities.push(entry);
		}
	});
	return cities;
}

export function defaultOrEnv(defaultValue, envName, trace = true) {
	let value = __ENV[envName];
	let isDefault = false;
	if (no(value)) {
		value = defaultValue;
		isDefault = true;
	}
	value = convertBooleanString(value);
	if (trace) {
		console.log(`${envName}: ${value}, is default: ${isDefault}, type ${typeof value}`);
	}
	return value;
}

export function no(value) {
	return value == null || value == undefined;
}

function convertBooleanString(value) {
	if (typeof value != "string") {
		return value;
	}

	switch(value.toLowerCase()) {
	case "false":
	case "no":
		return false;
	case "true":
	case "yes":
		return true;
	}

	return value;
}

