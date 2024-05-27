document.getElementById('uploadForm').addEventListener('submit', async function(event) {
    event.preventDefault();
    let formData = new FormData();
    let files = document.getElementById('files').files;

    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    let response = await fetch('http://localhost:8080/upload', { // Heroku 백엔드 URL로 수정
        method: 'POST',
        body: formData
    });

    if (response.ok) {
        let blob = await response.blob();
        let link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = 'modified_files.zip';
        link.click();
    } else {
        document.getElementById('result').textContent = 'Error: ' + response.statusText;
    }
});
