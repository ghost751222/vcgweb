Vue.prototype.$http = axios;


function uuidv4() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function saveTextAsFile(textToWrite, fileNameToSaveAs) {
    var textFileAsBlob = new Blob([textToWrite
    ],
        {
            type: 'text/plain'
        });
    var downloadLink = document.createElement("a");
    downloadLink.download = fileNameToSaveAs;
    downloadLink.innerHTML = "Download File";
    if (window.webkitURL != null) {
        downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob);
    }
    else {
        downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
        downloadLink.onclick = destroyClickedElement;
        downloadLink.style.display = "none";
        document.body.appendChild(downloadLink);
    }

    downloadLink.click();
}

var app = new Vue({
    el: "#app",
    data: {
        ws: null,
        url: 'chat',
        result: null,
        tabs: [],
        radios: [
            { 'value': 0, 'text': '合併下載' },
            { 'value': 1, 'text': '個別下載' }
        ],
        tabContent: '#tabContent',
        downloadTag: null,
        userName :null,
    }, methods: {
        upload() {

            let file = document.getElementById("file");
            const files = file.files;
            if (files.length == 0) {
                alert("請選擇檔案")
                file.focus();
                return
            }

            this.tabs = [];
            for (let i = 0; i < files.length; i++) {
                let fileName = files[i].name;
                let extension = fileName.split('.')[1];
                let newFileName = fileName.replace(extension, 'wav');
                let data = {
                    "fileName": newFileName,
                    "href": "#" + newFileName,
                    "statusText": "等待中",
                    "result": "", 'active': false, 'hide': true, 'fade': true
                }
                if (i == 0) data.active = true; data.hide = false;
                this.tabs.push(data)
            }

            var formData = new FormData(document.getElementById("form"));

            const config = {
                method: "post",
                url: url + "/recognizeUpload",
                data: formData
            }

            let modal = document.querySelector("#modal")
            modal.classList.toggle('hide');
            this.$http(config).then((res) => {
                if (res.status == 200) {
                    if (res.data != 'ok') {
                        location.reload()
                    }
                }
            }).catch(err => alert(err))
                .finally(() => {
                    modal.classList.toggle('hide');
                })
        }, download() {
            if (this.downloadTag == null) {
                alert('請選擇下載類型')
                return
            }
            if (this.downloadTag == 0) {
                let result = "";
                this.tabs.forEach(t => {
                    result = result + "-------------------" + t.fileName + "-------------------" + "\n"
                    result += t.result;
                })

                if (result) {
                    saveTextAsFile(result, "download.txt")
                }
            }
            else if (this.downloadTag == 1) {
                this.tabs.forEach(t => {
                    saveTextAsFile(t.result, t.fileName.replace('wav', 'txt'))
                })
            }
        }, tabClick(e) {
            const target = e.target;
            const fileName = target.getAttribute('fileName');
            this.tabs.forEach(t => {
                if (t.fileName == fileName) {
                    t.active = true;
                    t.hide = false;
                } else {
                    t.active = false;
                    t.hide = true;
                }
            })
        }, connectToWs() {


            if(localStorage.getItem('userName') != null){
               this.userName = localStorage.getItem('userName')
            }else{
              this.userName = uuidv4();
              localStorage.setItem('userName',this.userName)
            }



            var socket = new SockJS(this.url);
            this.ws = Stomp.over(socket);
            var userName = this.userName;
            var user = {
                'name': userName,
                headers: {}
            };


            const ws = this.ws;
            ws.connect(user, function (frame) {

                ws.subscribe('/user/queue/' + user.name, function (data) {
                    let res = JSON.parse(data.body);
                    let div = document.getElementById(res.fileName);
                    app.tabs.forEach(t => {
                        if (t.fileName == res.fileName) {

                            t.statusText = res.statusText
                            if (res.result) {
                                t.result += res.result + '\n'
                            }
                            if (t.statusText == '轉譯完成' || t.statusText == '服務器錯誤') {
                                t.fade = false;
                            } else
                                t.fade = true;
                            return
                        }
                    })
                });
            }, function (error) {
                console.log("WebSocket Connect error " + error);
            })
        }
    }, mounted() {
        this.connectToWs()

    }
})