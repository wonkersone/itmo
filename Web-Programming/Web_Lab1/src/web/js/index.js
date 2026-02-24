"use strict";

let currentR = 2;
let points = [];
const STORAGE_KEY = 'savedPoints';
let pageLoadTime = Date.now();

let canvas;
let ctx;

let hitSound;
let missSound;

function initSounds() {
    hitSound = new Audio('/sounds/hit.mp3');
    missSound = new Audio('/sounds/miss.mp3');

    // Настраиваем громкость
    hitSound.volume = 1;
    missSound.volume = 0.5;

    // Предзагрузка звуков
    hitSound.load();
    missSound.load();
}

function playHitSound() {
    if (hitSound) {
        hitSound.currentTime = 0; // Перематываем в начало
        hitSound.play().catch(e => {
            console.log("Не удалось воспроизвести звук попадания:", e);
        });
    }
}

function playMissSound() {
    if (missSound) {
        missSound.currentTime = 0; // Перематываем в начало
        missSound.play().catch(e => {
            console.log("Не удалось воспроизвести звук промаха:", e);
        });
    }
}

// function formatNumber(value) {
//     if (value === undefined || value === null) return '—';
//     const num = parseFloat(value);
//     if (isNaN(num)) return '—';
//
//     const str = num.toString();
//     if (str.includes('.')) {
//         return str.replace(/\.?0+$/, '');
//     }
//     return str;
// }

document.getElementById('y-coord').addEventListener('input', function(e) {
    let value = e.target.value;
    value = value.replace(/[^\d.,-]/g, '').replace(',', '.');

    if (value.indexOf('-') > 0) {
        value = value.replace(/-/g, '');
        if (value.length > 0) value = '-' + value;
    }
    if ((value.match(/-/g) || []).length > 1) {
        value = value.replace(/-/g, '');
        if (value.length > 0) value = '-' + value;
    }

    const dotCount = (value.match(/\./g) || []).length;
    if (dotCount > 1) {
        const parts = value.split('.');
        value = parts[0] + '.' + parts.slice(1).join('');
    }

    if (value.includes('.')) {
        const parts = value.split('.');
        if (parts[1] && parts[1].length > 3) {
            value = parts[0] + '.' + parts[1].substring(0, 3);
        }
    }

    e.target.value = value;
});

document.getElementById('r-value').addEventListener('input', function(e) {
    let value = e.target.value;
    value = value.replace(/[^\d.,-]/g, '').replace(',', '.');

    if (value.indexOf('-') > 0) {
        value = value.replace(/-/g, '');
        if (value.length > 0) value = '-' + value;
    }
    if ((value.match(/-/g) || []).length > 1) {
        value = value.replace(/-/g, '');
        if (value.length > 0) value = '-' + value;
    }

    const dotCount = (value.match(/\./g) || []).length;
    if (dotCount > 1) {
        const parts = value.split('.');
        value = parts[0] + '.' + parts.slice(1).join('');
    }

    if (value.includes('.')) {
        const parts = value.split('.');
        if (parts[1] && parts[1].length > 3) {
            value = parts[0] + '.' + parts[1].substring(0, 3);
        }
    }

    e.target.value = value;
});

function showError(message) {
    let errorElement = document.getElementById('y-error');
    if (!errorElement) {
        errorElement = document.createElement('div');
        errorElement.id = 'y-error';
        errorElement.className = 'validation-hint';
        document.getElementById('y-coord').parentNode.appendChild(errorElement);
    }
    errorElement.textContent = message;
    document.getElementById('y-coord').classList.add('invalid');
}

function hideError() {
    const errorElement = document.getElementById('y-error');
    if (errorElement) {
        errorElement.remove();
    }
    document.getElementById('y-coord').classList.remove('invalid');
}

document.getElementById('y-coord').addEventListener('blur', function(e) {
    const value = e.target.value;
    const numberValue = parseFloat(value.replace(',', '.'));

    if (value === '' || value === '-' || isNaN(numberValue)) {
        showError('Пожалуйста, введите число');
    } else if (numberValue < -3 || numberValue > 5) {
        showError('Число должно быть от -3 до 5');
    } else if (value.includes('.') && value.split('.')[1].length > 3) {
        showError('Максимальная точность - 3 знака после запятой');
    } else {
        hideError();
    }
});

document.getElementById('r-value').addEventListener('blur', function(e) {
    const value = e.target.value;
    const numberValue = parseFloat(value.replace(',', '.'));

    if (value === '' || value === '-' || isNaN(numberValue)) {
        let errorElement = document.getElementById('r-error');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.id = 'r-error';
            errorElement.className = 'validation-hint';
            document.getElementById('r-value').parentNode.appendChild(errorElement);
        }
        errorElement.textContent = 'Пожалуйста, введите число';
        document.getElementById('r-value').classList.add('invalid');
    } else if (numberValue < 1 || numberValue > 4) {
        let errorElement = document.getElementById('r-error');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.id = 'r-error';
            errorElement.className = 'validation-hint';
            document.getElementById('r-value').parentNode.appendChild(errorElement);
        }
        errorElement.textContent = 'Число должно быть от 1 до 4';
        document.getElementById('r-value').classList.add('invalid');
    } else if (value.includes('.') && value.split('.')[1].length > 3) {
        let errorElement = document.getElementById('r-error');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.id = 'r-error';
            errorElement.className = 'validation-hint';
            document.getElementById('r-value').parentNode.appendChild(errorElement);
        }
        errorElement.textContent = 'Максимальная точность - 3 знака после запятой';
        document.getElementById('r-value').classList.add('invalid');
    } else {
        const errorElement = document.getElementById('r-error');
        if (errorElement) {
            errorElement.remove();
        }
        document.getElementById('r-value').classList.remove('invalid');
    }
});

document.addEventListener('DOMContentLoaded', function() {
    pageLoadTime = Date.now();
    initSounds();

    canvas = document.getElementById('area-graph');
    if (canvas) {
        ctx = canvas.getContext('2d');
    } else {
        console.error("Canvas element 'area-graph' not found!");
        return;
    }

    loadPointsFromStorage();
    drawGraph(currentR);

    const xRadios = document.querySelectorAll('input[name="x"]');

    xRadios.forEach(radio => {
        radio.addEventListener('change', function () {
            drawGraph(currentR);
            points.forEach(point => {
                if (point.r === currentR) {
                    addPointToGraph(point.x, point.y, point.r, point.result);
                }
            });
        });
    });

    const rInput = document.getElementById('r-value');
    rInput.value = '';

    rInput.addEventListener('change', function() {
        const val = parseFloat(rInput.value.replace(',', '.'));
        if (!isNaN(val) && val >= 1 && val <= 4) {
            currentR = val;
            drawGraph(currentR);
            points.forEach(point => {
                if (point.r === currentR) {
                    addPointToGraph(point.x, point.y, point.r, point.result);
                }
            });
        }
    });

    document.getElementById('submit-btn').addEventListener('click', function(event) {
        event.preventDefault();
        submitForm();
    });

    document.getElementById('clear-results-btn').addEventListener('click', function(event) {
        event.preventDefault();
        clearResultsTable();
    });
});

function clearResultsTable() {
    if (confirm('Вы уверены, что хотите очистить таблицу результатов?')) {
        points = [];
        savePointsToStorage();
        updateResultsTable();
        drawGraph(currentR);
    }
}

function loadPointsFromStorage() {
    const savedPoints = localStorage.getItem(STORAGE_KEY);
    if (savedPoints) {
        try {
            points = JSON.parse(savedPoints);
            points = points.filter(p => p.x !== undefined && p.y !== undefined && p.r !== undefined);
            updateResultsTable();
        } catch (e) {
            console.error('Ошибка загрузки точек:', e);
            points = [];
        }
    }
}

function savePointsToStorage() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(points));
}

function updateResultsTable() {
    const resultsTableBody = document.getElementById('results-body');
    if (!resultsTableBody) return;

    resultsTableBody.innerHTML = '';

    if (points.length === 0) {
        resultsTableBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 20px;">
                    пока ничего нет
                </td>
            </tr>
        `;
        return;
    }

    for (let i = points.length - 1; i >= 0; i--) {
        const data = points[i];
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
            <td>${data.x}</td>
            <td>${data.yDisplay !== undefined ? data.yDisplay : '—'}</td>
            <td>${data.rDisplay !== undefined ? data.rDisplay : '—'}</td>
            <td class="${data.result ? 'hit' : 'miss'}">${data.result ? 'Попадание' : 'Промах'}</td>
            <td>${data.currentTime || '—'}</td>
            <td>${data.workTime || '—'}</td>
        `;
        resultsTableBody.appendChild(newRow);
    }
}

function submitForm() {
    const requestStartTime = Date.now();

    const xRadio = document.querySelector('input[name="x"]:checked');
    if (!xRadio) {
        alert("Пожалуйста, выберите значение X.");
        return;
    }
    const xValue = parseFloat(xRadio.value);

    const yInput = document.getElementById('y-coord');
    const yValueRaw = yInput.value.replace(',', '.');
    const yValue = parseFloat(yValueRaw);
    const yDisplay = yInput.value;

    if (isNaN(yValue) || yValue < -3 || yValue > 5 || (yValueRaw.includes('.') && yValueRaw.split('.')[1].length > 3)) {
        yInput.focus();
        if (yInput.value === '') showError('Пожалуйста, введите число');
        return;
    }
    hideError();

    const rInput = document.getElementById('r-value');
    const rValueRaw = rInput.value.replace(',', '.');
    const rValue = parseFloat(rValueRaw);
    const rDisplay = rInput.value;

    if (isNaN(rValue) || rValue < 1 || rValue > 4) {
        alert("Пожалуйста, введите корректное число для R (от 1 до 4).");
        rInput.focus();
        return;
    }

    const params = new URLSearchParams();
    params.append('x', xValue.toString());
    params.append('y', yValue.toString());
    params.append('r', rValue.toString());

    fetch('/fcgi-bin/?' + params.toString(), {
        method: 'GET'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const hit = checkPointInArea(xValue, yValue, rValue);
            const workTime = Date.now() - requestStartTime;

            if (hit) {
                playHitSound();
            } else {
                playMissSound();
            }

            const newPoint = {
                x: xValue,
                y: yValue,
                yDisplay: yDisplay,
                r: rValue,
                rDisplay: rDisplay,
                result: hit,
                currentTime: new Date().toLocaleString(),
                workTime: workTime + ' ms'
            };

            points.push(newPoint);
            savePointsToStorage();
            updateResultsTable();
            addPointToGraph(newPoint.x, newPoint.y, newPoint.r, newPoint.result);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            const hit = checkPointInArea(xValue, yValue, rValue);
            const workTime = Date.now() - requestStartTime;

            if (hit) {
                playHitSound();
            } else {
                playMissSound();
            }

            const newPoint = {
                x: xValue,
                y: yValue,
                yDisplay: yDisplay,
                r: rValue,
                rDisplay: rDisplay,
                result: hit,
                currentTime: new Date().toLocaleString(),
                workTime: workTime + ' ms'
            };

            points.push(newPoint);
            savePointsToStorage();
            updateResultsTable();
            addPointToGraph(newPoint.x, newPoint.y, newPoint.r, newPoint.result);

            alert("Сервер недоступен. Используется локальная проверка.");
        });
}

function checkPointInArea(x, y, r) {
    const inQuarter1 = x >= 0 && y >= 0 && (x * x + y * y <= r * r);
    const inQuarter2 = false;
    const inQuarter3 = x <= 0 && y <= 0 && x >= -r && y >= -r;
    const inQuarter4 = x >= 0 && y <= 0 && x <= r/2 && y >= -r && y >= -2 * x - r;

    return inQuarter1 || inQuarter2 || inQuarter3 || inQuarter4;
}

function addPointToGraph(x, y, r, hit) {
    if (r !== currentR) return;
    if (!ctx || !canvas) return;

    const width = canvas.width;
    const height = canvas.height;
    const center = { x: width / 2, y: height / 2 };
    const scale = (width / 2) / (r * 1.5);

    const pointX = center.x + x * scale;
    const pointY = center.y - y * scale;

    ctx.fillStyle = hit ? '#4CAF50' : '#FF6B6B';
    ctx.beginPath();
    ctx.arc(pointX, pointY, 4, 0, Math.PI * 2);
    ctx.fill();
}

function drawGraph(r) {
    if (!ctx || !canvas){
        console.error("Canvas or context not available now!");
        return;
    }

    const width = canvas.width;
    const height = canvas.height;
    const center = { x: width / 2, y: height / 2 };
    const scale = (width / 2) / (r * 1.5);

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = 'rgba(0, 180, 216, 0.3)';
    //I четв
    ctx.beginPath();
    ctx.arc(center.x, center.y, r * scale, 0, -Math.PI / 2, true);
    ctx.lineTo(center.x, center.y);
    ctx.closePath();
    ctx.fill();
    //III четв
    ctx.fillRect(center.x - r * scale, center.y, r * scale, r * scale);
    //IV четв
    ctx.beginPath();
    ctx.moveTo(center.x, center.y);
    ctx.lineTo(center.x + (r/2) * scale, center.y);
    ctx.lineTo(center.x, center.y + r * scale);
    ctx.closePath();
    ctx.fill();

    //оси
    ctx.strokeStyle = '#00b4d8';
    ctx.lineWidth = 2;

    ctx.beginPath();
    ctx.moveTo(50, center.y);
    ctx.lineTo(width - 50, center.y);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(center.x, 50);
    ctx.lineTo(center.x, height - 50);
    ctx.stroke();

    ctx.fillStyle = '#00b4d8';
    ctx.font = '12px Arial';
    ctx.fillText("X", width - 40, center.y - 10);
    ctx.fillText("Y", center.x + 10, 40);
    ctx.fillText("0", center.x + 5, center.y - 10);

    const labels = [-r, -r/2, r/2, r];
    labels.forEach(val => {
        if (val !== 0) {
            const xPos = center.x + val * scale;
            const yPos = center.y - val * scale;

            ctx.fillText(val.toString(), xPos, center.y + 15);
            ctx.fillText(val.toString(), center.x - 20, yPos + 5);
        }
    });

    points.forEach(point => {
        if (point.r === r) {
            addPointToGraph(point.x, point.y, point.r, point.result);
        }
    });
}