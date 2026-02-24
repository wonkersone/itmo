"use strict";

let currentR = 2;
let canvas;
let ctx;

document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM loaded, initializing graph...");

    canvas = document.getElementById('area-graph');
    if (canvas) {
        ctx = canvas.getContext('2d');
        console.log("Canvas and context initialized");

        const rRadios = document.querySelectorAll('input[name="r"]');
        let foundR = false;
        rRadios.forEach(radio => {
            if (radio.checked) {
                const rValue = parseFloat(radio.value);
                if (!isNaN(rValue)) {
                    currentR = rValue;
                    foundR = true;
                    console.log("Initial R value:", currentR);
                }
            }
        });

        if (!foundR && rRadios.length > 0) {
            rRadios[0].checked = true;
            currentR = parseFloat(rRadios[0].value);
        }

        drawGraph(currentR);
        drawAllPoints();

        setupEventHandlers();

    } else {
        console.error("Canvas element 'area-graph' not found!");
    }
});

function setupEventHandlers() {
    setupValidationHandlers();

    setupGraphClickHandler();

    setupFormValidation();

    const rRadios = document.querySelectorAll('input[name="r"]');
    rRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            const rValue = parseFloat(radio.value);
            if (!isNaN(rValue)) {
                currentR = rValue;
                console.log("R changed to:", currentR);
                drawGraph(currentR);
                drawAllPoints();
                hideError('r');
            }
        });
    });

    const xRadios = document.querySelectorAll('input[name="x"]');
    xRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            document.getElementById('x-graph').value = '';
            drawGraph(currentR);
            drawAllPoints();
            hideError('x');
        });
    });
}

function validateNumber(value, min, max, fieldName, allowDecimal = true) {
    if (value === '' || value === '-') {
        return { isValid: false, message: 'Пожалуйста, введите число' };
    }

    if (!/^-?\d*[,.]?\d*$/.test(value)) {
        return { isValid: false, message: 'Пожалуйста, введите корректное число' };
    }

    const numberValue = parseFloat(value.replace(',', '.'));

    if (isNaN(numberValue)) {
        return { isValid: false, message: 'Пожалуйста, введите корректное число' };
    }

    if (numberValue < min || numberValue > max) {
        return { isValid: false, message: `Число должно быть от ${min} до ${max}` };
    }

    if (allowDecimal) {
        if (value.includes('.') && value.split('.')[1].length > 3) {
            return { isValid: false, message: 'Максимальная точность - 3 знака после запятой' };
        }
        if (value.includes(',') && value.split(',')[1].length > 3) {
            return { isValid: false, message: 'Максимальная точность - 3 знака после запятой' };
        }
    } else {
        if (value.includes('.') || value.includes(',')) {
            return { isValid: false, message: 'Значение должно быть целым числом' };
        }
        if (!Number.isInteger(numberValue)) {
            return { isValid: false, message: 'Значение должно быть целым числом' };
        }
    }

    return { isValid: true, numberValue: numberValue };
}

function showError(field, message) {
    const errorElement = document.getElementById(field + '-error');
    if (errorElement) {
        errorElement.textContent = message;
    }
    const inputElement = document.getElementById(field + '-coord') || document.getElementById(field + '-value');
    if (inputElement) {
        inputElement.classList.add('invalid');
    }
}

function hideError(field) {
    const errorElement = document.getElementById(field + '-error');
    if (errorElement) {
        errorElement.textContent = '';
    }
    const inputElement = document.getElementById(field + '-coord') || document.getElementById(field + '-value');
    if (inputElement) {
        inputElement.classList.remove('invalid');
    }
}

function setupValidationHandlers() {
    const yInput = document.getElementById('y-coord');

    if (yInput) {
        yInput.addEventListener('input', function(e) {
            let value = e.target.value;
            value = value.replace(/[^\d.,-]/g, '');

            value = value.replace(',', '.');

            if (value.includes('.')) {
                let parts = value.split('.');
                if (parts[1] && parts[1].length > 3) {
                    parts[1] = parts[1].substring(0, 3);
                    value = parts[0] + '.' + parts[1];
                }
            }

            e.target.value = value;

            const result = validateNumber(value, -3, 5, 'y');
            if (!result.isValid && value !== '' && value !== '-') {
                showError('y', result.message);
            } else {
                hideError('y');
            }
        });

        yInput.addEventListener('blur', function(e) {
            let value = e.target.value;
            value = value.replace(',', '.');

            if (value.includes('.')) {
                let parts = value.split('.');
                if (parts[1] && parts[1].length > 3) {
                    parts[1] = parts[1].substring(0, 3);
                    e.target.value = parts[0] + '.' + parts[1];
                }
            }

            const result = validateNumber(e.target.value, -3, 5, 'y');
            if (!result.isValid) {
                showError('y', result.message);
            } else {
                hideError('y');
            }
        });
    }
}

function setupGraphClickHandler() {
    if (!canvas) {
        console.error("Canvas not available for click handler");
        return;
    }

    canvas.addEventListener('click', function(e) {
        console.log("Canvas clicked");

        const rRadios = document.querySelectorAll('input[name="r"]');
        let selectedR = null;

        rRadios.forEach(radio => {
            if (radio.checked) {
                selectedR = parseFloat(radio.value);
            }
        });

        console.log("Selected R:", selectedR);

        if (selectedR === null) {
            alert("Пожалуйста, установите радиус R перед кликом по графику");
            return;
        }

        const rValue = selectedR;

        const rect = canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        const mathX = convertToMathX(x, rValue);
        const mathY = convertToMathY(y, rValue);

        console.log(`Canvas coordinates: (${x}, ${y}) -> Math coordinates: (${mathX.toFixed(3)}, ${mathY.toFixed(3)})`);

        if (mathX < -5 || mathX > 3) {
            alert("Координата X должна быть в диапазоне от -5 до 3");
            return;
        }

        if (mathY < -3 || mathY > 5) {
            alert("Координата Y должна быть в диапазоне от -3 до 5");
            return;
        }

        setXValueFromGraph(mathX);

        const roundedY = Math.round(mathY * 1000) / 1000;
        document.getElementById('y-coord').value = roundedY;

        hideError('x');
        hideError('y');
        hideError('r');

        if (validateForm()) {
            console.log("Form validated successfully, submitting...");
            document.getElementById('point-form').submit();
        } else {
            console.log("Form validation failed");
        }
    });
}

function convertToMathX(canvasX, r) {
    const width = canvas.width;
    const center = width / 2;
    const scale = (width / 2) / (r * 1.2);
    return (canvasX - center) / scale;
}

function convertToMathY(canvasY, r) {
    const height = canvas.height;
    const center = height / 2;
    const scale = (height / 2) / (r * 1.2);
    return (center - canvasY) / scale;
}

function setXValueFromGraph(x) {
    const roundedX = Math.round(x * 1000) / 1000;

    document.getElementById('x-graph').value = roundedX;

    const xRadios = document.querySelectorAll('input[name="x"]');
    xRadios.forEach(radio => {
        radio.checked = false;
    });

    hideError('x');
    console.log(`X value set from graph: ${roundedX}`);
}

function validateForm() {
    const xRadio = document.querySelector('input[name="x"]:checked');
    const xGraphValue = document.getElementById('x-graph').value;
    const yInput = document.getElementById('y-coord');
    const rRadio = document.querySelector('input[name="r"]:checked');

    let isValid = true;

    if (!xRadio && (!xGraphValue || xGraphValue === '')) {
        showError('x', 'Пожалуйста, выберите значение X или кликните по графику');
        isValid = false;
    } else {
        if (xGraphValue && xGraphValue !== '') {
            const xValidation = validateNumber(xGraphValue, -5, 3, 'x');
            if (!xValidation.isValid) {
                showError('x', xValidation.message);
                isValid = false;
            } else {
                hideError('x');
            }
        } else {
            const xValue = xRadio.value;
            const xValidation = validateNumber(xValue, -5, 3, 'x', false);
            if (!xValidation.isValid) {
                showError('x', xValidation.message);
                isValid = false;
            } else {
                hideError('x');
            }
        }
    }

    const yValidation = validateNumber(yInput.value, -3, 5, 'y');
    if (!yValidation.isValid) {
        showError('y', yValidation.message);
        isValid = false;
    } else {
        hideError('y');
    }

    if (!rRadio) {
        showError('r', 'Пожалуйста, выберите значение R');
        isValid = false;
    } else {
        hideError('r');
    }

    return isValid;
}

function setupFormValidation() {
    const form = document.getElementById('point-form');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateForm()) {
                e.preventDefault();
                alert('Пожалуйста, исправьте ошибки в форме перед отправкой');
            }
        });
    }
}

function drawGraph(r) {
    if (!ctx || !canvas) {
        console.error("Canvas or context not available for drawing!");
        return;
    }

    const width = canvas.width;
    const height = canvas.height;
    const center = { x: width / 2, y: height / 2 };
    const scale = (width / 2) / (r * 1.2);

    ctx.clearRect(0, 0, width, height);

    ctx.fillStyle = 'rgba(0, 180, 216, 0.3)';

    ctx.beginPath();
    ctx.moveTo(center.x, center.y);
    ctx.lineTo(center.x + (r/2) * scale, center.y);
    ctx.lineTo(center.x, center.y - (r/2) * scale);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.arc(center.x, center.y, (r/2) * scale, Math.PI, Math.PI * 1.5, false);
    ctx.lineTo(center.x, center.y);
    ctx.closePath();
    ctx.fill();

    ctx.fillRect(center.x, center.y, (r/2) * scale, r * scale);

    ctx.strokeStyle = '#00b4d8';
    ctx.lineWidth = 2;

    ctx.beginPath();
    ctx.moveTo(0, center.y);
    ctx.lineTo(width, center.y);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(center.x, 0);
    ctx.lineTo(center.x, height);
    ctx.stroke();

    ctx.fillStyle = '#00b4d8';

    ctx.beginPath();
    ctx.moveTo(width - 10, center.y - 5);
    ctx.lineTo(width, center.y);
    ctx.lineTo(width - 10, center.y + 5);
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(center.x - 5, 10);
    ctx.lineTo(center.x, 0);
    ctx.lineTo(center.x + 5, 10);
    ctx.fill();

    ctx.font = '14px Arial';
    ctx.fillStyle = '#00b4d8';
    ctx.fillText("X", width - 20, center.y - 10);
    ctx.fillText("Y", center.x + 10, 20);

    const labels = [-r, -r/2, r/2, r];
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillStyle = '#e0e0e0';

    labels.forEach(val => {
        if (val !== 0) {
            const xPos = center.x + val * scale;
            ctx.fillText(val.toString(), xPos, center.y + 15);
        }
    });

    labels.forEach(val => {
        if (val !== 0) {
            const yPos = center.y - val * scale;
            ctx.fillText(val.toString(), center.x - 15, yPos);
        }
    });

    ctx.fillText("0", center.x - 10, center.y + 15);
}

function addPointToGraph(x, y, r, hit) {
    if (!ctx || !canvas) return;

    const width = canvas.width;
    const height = canvas.height;
    const center = { x: width / 2, y: height / 2 };
    const scale = (width / 2) / (r * 1.2);

    const pointX = center.x + x * scale;
    const pointY = center.y - y * scale;

    ctx.fillStyle = hit ? '#4CAF50' : '#FF6B6B';
    ctx.beginPath();
    ctx.arc(pointX, pointY, 4, 0, Math.PI * 2);
    ctx.fill();

    ctx.strokeStyle = '#ffffff';
    ctx.lineWidth = 1;
    ctx.stroke();
}

function drawAllPoints() {
    if (!ctx || !canvas) {
        console.log("Canvas or context not available");
        return;
    }

    console.log("Drawing all points for R =", currentR);
    console.log("Points history:", window.pointsHistory);

    drawGraph(currentR);

    if (window.pointsHistory && window.pointsHistory.length > 0) {
        window.pointsHistory.forEach(point => {
            if (Math.abs(point.r - currentR) < 0.001) {
                console.log("Drawing point:", point);
                addPointToGraph(point.x, point.y, point.r, point.hit);
            }
        });
    } else {
        console.log("No points history available");
    }
}

function updatePointsHistory(newPoint) {
    if (!window.pointsHistory) {
        window.pointsHistory = [];
    }
    window.pointsHistory.unshift(newPoint);
    drawGraph(currentR);
    drawAllPoints();
}