"use strict";

let currentR = 2;
let canvas;
let ctx;

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM loaded, initializing graph...");

    canvas = document.getElementById('area-graph');
    if (canvas) {
        ctx = canvas.getContext('2d');
        console.log("Canvas and context initialized");

        // Инициализация значения R из выбранной radio button
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

        // Если R не выбран, устанавливаем первый доступный
        if (!foundR && rRadios.length > 0) {
            rRadios[0].checked = true;
            currentR = parseFloat(rRadios[0].value);
        }

        // Первоначальная отрисовка графика
        drawGraph(currentR);
        drawAllPoints();

        // Настройка обработчиков
        setupEventHandlers();

    } else {
        console.error("Canvas element 'area-graph' not found!");
    }
});

function setupEventHandlers() {
    // Обработчики валидации для поля Y
    setupValidationHandlers();

    // Обработчик клика по графику
    setupGraphClickHandler();

    // Обработчик отправки формы
    setupFormValidation();

    // Обработчики изменения R для перерисовки графика
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

    // Обработчики изменения X для перерисовки графика
    const xRadios = document.querySelectorAll('input[name="x"]');
    xRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            // При выборе радиокнопки X очищаем скрытое поле графика
            document.getElementById('x-graph').value = '';
            drawGraph(currentR);
            drawAllPoints();
            hideError('x');
        });
    });
}

// Функции валидации
function validateNumber(value, min, max, fieldName, allowDecimal = true) {
    if (value === '' || value === '-') {
        return { isValid: false, message: 'Пожалуйста, введите число' };
    }

    // Проверка на допустимые символы
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

    // Проверка точности (максимум 3 знака после запятой)
    if (allowDecimal) {
        if (value.includes('.') && value.split('.')[1].length > 3) {
            return { isValid: false, message: 'Максимальная точность - 3 знака после запятой' };
        }
        if (value.includes(',') && value.split(',')[1].length > 3) {
            return { isValid: false, message: 'Максимальная точность - 3 знака после запятой' };
        }
    } else {
        // Для целых чисел проверяем, что нет дробной части
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
            // Ограничение ввода только цифр, минуса, точки и запятой
            let value = e.target.value;
            value = value.replace(/[^\d.,-]/g, '');

            // Ограничение точности в реальном времени
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
            // Финализация значения при потере фокуса
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

// Обработчик клика по графику
function setupGraphClickHandler() {
    if (!canvas) {
        console.error("Canvas not available for click handler");
        return;
    }

    canvas.addEventListener('click', function(e) {
        console.log("Canvas clicked");

        // Получаем ВСЕ radio кнопки R и проверяем, есть ли выбранная
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

        // Конвертируем координаты canvas в математические координаты
        const mathX = convertToMathX(x, rValue);
        const mathY = convertToMathY(y, rValue);

        console.log(`Canvas coordinates: (${x}, ${y}) -> Math coordinates: (${mathX.toFixed(3)}, ${mathY.toFixed(3)})`);

        // Проверяем координаты
        if (mathX < -5 || mathX > 3) {
            alert("Координата X должна быть в диапазоне от -5 до 3");
            return;
        }

        if (mathY < -3 || mathY > 5) {
            alert("Координата Y должна быть в диапазоне от -3 до 5");
            return;
        }

        // Устанавливаем значения в форму
        setXValueFromGraph(mathX);

        // Ограничиваем Y до 3 знаков после запятой
        const roundedY = Math.round(mathY * 1000) / 1000;
        document.getElementById('y-coord').value = roundedY;

        // Снимаем ошибки
        hideError('x');
        hideError('y');
        hideError('r');

        // Валидируем и отправляем форму
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
    // Ограничиваем X до 3 знаков после запятой
    const roundedX = Math.round(x * 1000) / 1000;

    // Устанавливаем значение в скрытое поле для графика
    document.getElementById('x-graph').value = roundedX;

    // Снимаем выделение с радиокнопок
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

    // Валидация X: либо выбрана радиокнопка, либо установлено значение с графика
    if (!xRadio && (!xGraphValue || xGraphValue === '')) {
        showError('x', 'Пожалуйста, выберите значение X или кликните по графику');
        isValid = false;
    } else {
        // Если значение установлено с графика, проверяем его
        if (xGraphValue && xGraphValue !== '') {
            const xValidation = validateNumber(xGraphValue, -5, 3, 'x');
            if (!xValidation.isValid) {
                showError('x', xValidation.message);
                isValid = false;
            } else {
                hideError('x');
            }
        } else {
            // Если выбрана радиокнопка, проверяем что значение целое
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

    // Валидация Y
    const yValidation = validateNumber(yInput.value, -3, 5, 'y');
    if (!yValidation.isValid) {
        showError('y', yValidation.message);
        isValid = false;
    } else {
        hideError('y');
    }

    // Валидация R
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

// Функции для работы с графиком
function drawGraph(r) {
    if (!ctx || !canvas) {
        console.error("Canvas or context not available for drawing!");
        return;
    }

    const width = canvas.width;
    const height = canvas.height;
    const center = { x: width / 2, y: height / 2 };
    const scale = (width / 2) / (r * 1.2);

    // Очищаем canvas
    ctx.clearRect(0, 0, width, height);

    // Отрисовка областей
    ctx.fillStyle = 'rgba(0, 180, 216, 0.3)';

    // I четверть - равнобедренный прямоугольный треугольник (прямой угол в начале координат)
    ctx.beginPath();
    ctx.moveTo(center.x, center.y);
    ctx.lineTo(center.x + (r/2) * scale, center.y);
    ctx.lineTo(center.x, center.y - (r/2) * scale);
    ctx.closePath();
    ctx.fill();

    // II четверть - четверть круга радиусом R/2
    ctx.beginPath();
    ctx.arc(center.x, center.y, (r/2) * scale, Math.PI, Math.PI * 1.5, false);
    ctx.lineTo(center.x, center.y);
    ctx.closePath();
    ctx.fill();

    // III четверть - ничего (пропускаем)

    // IV четверть - прямоугольник (левый верхний угол в начале координат)
    ctx.fillRect(center.x, center.y, (r/2) * scale, r * scale);

    // Оси координат
    ctx.strokeStyle = '#00b4d8';
    ctx.lineWidth = 2;

    // Ось X
    ctx.beginPath();
    ctx.moveTo(0, center.y);
    ctx.lineTo(width, center.y);
    ctx.stroke();

    // Ось Y
    ctx.beginPath();
    ctx.moveTo(center.x, 0);
    ctx.lineTo(center.x, height);
    ctx.stroke();

    // Стрелки для осей
    ctx.fillStyle = '#00b4d8';

    // Стрелка для оси X
    ctx.beginPath();
    ctx.moveTo(width - 10, center.y - 5);
    ctx.lineTo(width, center.y);
    ctx.lineTo(width - 10, center.y + 5);
    ctx.fill();

    // Стрелка для оси Y
    ctx.beginPath();
    ctx.moveTo(center.x - 5, 10);
    ctx.lineTo(center.x, 0);
    ctx.lineTo(center.x + 5, 10);
    ctx.fill();

    // Подписи осей
    ctx.font = '14px Arial';
    ctx.fillStyle = '#00b4d8';
    ctx.fillText("X", width - 20, center.y - 10);
    ctx.fillText("Y", center.x + 10, 20);

    // Подписи значений
    const labels = [-r, -r/2, r/2, r];
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillStyle = '#e0e0e0';

    // Подписи для оси X
    labels.forEach(val => {
        if (val !== 0) {
            const xPos = center.x + val * scale;
            ctx.fillText(val.toString(), xPos, center.y + 15);
        }
    });

    // Подписи для оси Y
    labels.forEach(val => {
        if (val !== 0) {
            const yPos = center.y - val * scale;
            ctx.fillText(val.toString(), center.x - 15, yPos);
        }
    });

    // Подпись начала координат
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

    // Рисуем точку
    ctx.fillStyle = hit ? '#4CAF50' : '#FF6B6B';
    ctx.beginPath();
    ctx.arc(pointX, pointY, 4, 0, Math.PI * 2);
    ctx.fill();

    // Добавляем обводку для лучшей видимости
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

    // Перерисовываем график сначала
    drawGraph(currentR);

    // Рисуем точки для текущего R
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

// Функция для обновления истории точек (вызывается после успешной отправки формы)
function updatePointsHistory(newPoint) {
    if (!window.pointsHistory) {
        window.pointsHistory = [];
    }
    window.pointsHistory.unshift(newPoint);
    drawGraph(currentR);
    drawAllPoints();
}