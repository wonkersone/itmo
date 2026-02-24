
const scale = 40;
let canvas, ctx, originX, originY;

function init() {
    canvas = document.getElementById('graphCanvas');
    if (canvas) {
        ctx = canvas.getContext('2d');
        originX = canvas.width / 2;
        originY = canvas.height / 2;


        canvas.onclick = handleCanvasClick;
    }
}

function getCurrentR() {
    const hiddenR = document.getElementById("form:hiddenR");
    if (hiddenR && hiddenR.value) {
        let val = parseFloat(hiddenR.value);
        if (!isNaN(val) && val > 0) return val;
    }
    return 1;
}

function redrawCanvas() {

    if (!canvas || !ctx) init();
    if (!ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const r = getCurrentR();

    drawAxes();
    drawShape(r);
    drawSavedPoints();
}

function drawShape(r) {
    if(!r) return;
    if(!ctx) return;

    ctx.fillStyle = 'rgba(33, 150, 243, 0.5)';
    ctx.strokeStyle = '#000';
    ctx.lineWidth = 1;

    const rPx = r * scale;
    const halfRPx = (r / 2) * scale;


    ctx.beginPath();
    ctx.moveTo(originX, originY);
    ctx.arc(originX, originY, rPx, -Math.PI / 2, 0, false);
    ctx.fill();
    ctx.stroke();

    ctx.beginPath();
    ctx.rect(originX - halfRPx, originY, halfRPx, rPx);
    ctx.fill();
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(originX, originY);
    ctx.lineTo(originX + halfRPx, originY);
    ctx.lineTo(originX, originY + halfRPx);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
}

function drawAxes() {
    if(!ctx) return;

    ctx.beginPath();
    ctx.strokeStyle = '#333';
    ctx.lineWidth = 1.5;

    ctx.moveTo(0, originY); ctx.lineTo(canvas.width, originY);
    ctx.moveTo(originX, 0); ctx.lineTo(originX, canvas.height);
    ctx.stroke();


    ctx.fillStyle = '#333';
    ctx.beginPath(); ctx.moveTo(canvas.width - 10, originY - 5); ctx.lineTo(canvas.width, originY); ctx.lineTo(canvas.width - 10, originY + 5); ctx.fill();
    ctx.beginPath(); ctx.moveTo(originX - 5, 10); ctx.lineTo(originX, 0); ctx.lineTo(originX + 5, 10); ctx.fill();

    const r = getCurrentR();
    const tickSz = 4;
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    const steps = [r, r/2, -r/2, -r];
    const labels = ["R", "R/2", "-R/2", "-R"];

    steps.forEach((val, i) => {
        if(!val) return;
        const px = originX + val * scale;
        ctx.beginPath(); ctx.moveTo(px, originY - tickSz); ctx.lineTo(px, originY + tickSz); ctx.stroke();
        ctx.fillText(labels[i], px, originY + 15);

        const py = originY - val * scale;
        ctx.beginPath(); ctx.moveTo(originX - tickSz, py); ctx.lineTo(originX + tickSz, py); ctx.stroke();
        ctx.fillText(labels[i], originX + 25, py);
    });
}

function drawSavedPoints() {
    if(!ctx) return;

    const table = document.getElementById('form:results-table');
    if (!table) return;
    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');

    for (let i = 0; i < rows.length; i++) {
        const cells = rows[i].getElementsByTagName('td');
        if (cells.length < 4) continue;

        const xText = cells[0].innerText.trim().replace(',', '.');
        const yText = cells[1].innerText.trim().replace(',', '.');
        const resText = cells[3].innerText;

        const x = parseFloat(xText);
        const y = parseFloat(yText);

        if (isNaN(x) || isNaN(y)) continue;

        const isHit = resText.includes('Попадание') || resText.includes('Hit');

        const xPixel = originX + x * scale;
        const yPixel = originY - y * scale;

        ctx.beginPath();
        ctx.arc(xPixel, yPixel, 5, 0, Math.PI * 2);
        ctx.fillStyle = isHit ? '#00e676' : '#ff1744';
        ctx.strokeStyle = '#000';
        ctx.fill();
        ctx.stroke();
    }
}

function handleCanvasClick(e) {
    if (!canvas) return;

    const rect = canvas.getBoundingClientRect();
    const xPixel = e.clientX - rect.left;
    const yPixel = e.clientY - rect.top;

    const x = (xPixel - originX) / scale;
    const y = (originY - yPixel) / scale;

    let clampedX = x;
    if(clampedX > 3) clampedX = 3;
    if(clampedX < -3) clampedX = -3;

    let clampedY = y;
    if (clampedY > 5) clampedY = 5;
    if (clampedY < -3) clampedY = -3;


    const yInput = document.getElementById('form:yInput');
    if(yInput) yInput.value = clampedY.toFixed(4);


    if (window.sendGraphRequest) {
        console.log("Отправка координат:", clampedX, clampedY);


        sendGraphRequest([
            {name: 'x', value: clampedX.toFixed(4)},
            {name: 'y', value: clampedY.toFixed(4)}
        ]);
    } else {
        console.error("sendGraphRequest не найден!");
    }
}