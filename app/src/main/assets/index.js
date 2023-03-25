"use strict";
/**
 * Simple script to make text elements on the page editable,
 * and to save and restore the page across page loads.
 * 
 * Note: if running on the file:// protocol, some browsers may
 * disable access to localStorage.
 */
//import { initializeApp } from "https://www.gstatic.com/firebasejs/9.17.1/firebase-app.js";
//  import { getAnalytics } from "https://www.gstatic.com/firebasejs/9.17.1/firebase-analytics.js";
  // TODO: Add SDKs for Firebase products that you want to use
  // https://firebase.google.com/docs/web/setup#available-libraries

  // Your web app's Firebase configuration
  // For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
    apiKey: "AIzaSyDDGYXxSpvumafdYg8eV38BUcu5D5Ig6jU",
    authDomain: "uptoskills-d8b32.firebaseapp.com",
    databaseURL: "https://uptoskills-d8b32-default-rtdb.firebaseio.com",
    projectId: "uptoskills-d8b32",
    storageBucket: "uptoskills-d8b32.appspot.com",
    messagingSenderId: "805497522425",
    appId: "1:805497522425:web:64ecf9538e5708d8dafa28",
    measurementId: "G-8X96PN6Y5D"
  };

  // Initialize Firebase
  firebase.initializeApp(firebaseConfig);
//  const analytics = getAnalytics(app);

 // Initialize Cloud Firestore and get a reference to the service
 const db = firebase.firestore();
 var url_string = window.location.href
 var url = new URL(url_string);
 var message= url.searchParams.get("message");
(function() {
    var VERSION = 1.2;

    var USE_CONTENTEDITABLE = !('designMode' in document);

    // Source https://stackoverflow.com/questions/494143/creating-a-new-dom-element-from-an-html-string-using-built-in-dom-methods-or-pro
    function htmlToElement(html) {
        var documentFragment = document.createDocumentFragment();
        var template = document.createElement('template');
        template.innerHTML = html.trim();
        for (var i = 0, e = template.content.childNodes.length; i < e; i++) {
            documentFragment.appendChild(template.content.childNodes[i].cloneNode(true));
        }
        return documentFragment;
    }

    function supportsLocalStorage() {
        if (!('localStorage' in window)) return false;
        try {
            localStorage.setItem('test', 'true');
            localStorage.getItem('test');
            localStorage.removeItem('test');
            return true;
        } catch (e) {
            return false;
        }
    }

    function supportsTemplate() {
        return ('content' in document.createElement('template'));
    }

    var hasLocalStorage = supportsLocalStorage();
    var hasTemplate = supportsTemplate();

    function savePage() {
        localStorage.setItem('page', escape(document.getElementById('save').innerHTML));
    }

    function getSavedPage() {
        var pageStr = localStorage.getItem('page');
        if (!(pageStr && pageStr.length)) return null;
        return unescape(pageStr);
    }

    function setDesignMode(mode) {
        return function() {
            if ('designMode' in document) {
                document.designMode = mode;
            }
        };
    }

    function restoreSavedPage() {
        var savedPage = getSavedPage();
        if (savedPage) {
            document.getElementById('save').innerHTML = savedPage;
        }
    }

    function getDownloadLink(data, type) {
        var URL = (window.URL || window.webkitURL);
        var Blob = (window.Blob || window.MozBlob || window.WebKitBlob);
        var file = new Blob([String.fromCharCode(0xFEFF), data], { type: type }); // prepend BOM
        return URL.createObjectURL(file);
    }

    // Clone HTML node, but remove extraneous elements and make read-only
    function getPageContents() {
        var baseEl = document.documentElement.cloneNode(true);
        baseEl.querySelector('body').removeAttribute('spellcheck');
        var elsToRemove = baseEl.querySelectorAll('script, iframe, #document-controls, #github-link');
        for (var i = 0, e = elsToRemove.length; i < e; i++) {
            elsToRemove[i].parentElement.removeChild(elsToRemove[i]);
        }

        if (USE_CONTENTEDITABLE) {
            var elsToReset = baseEl.querySelectorAll('[contenteditable]');
            for (var i = 0, e = elsToReset.length; i < e; i++) {
                elsToReset[i].removeAttribute('contenteditable');
                elsToReset[i].removeAttribute('spellcheck');
            }
        }

        return baseEl.innerHTML;
    }

    function addPage() {
        var sheetContainer = document.querySelector('.sheet').parentElement;
        var sheetHtml =
        `<section class="sheet">
            <aside></aside>
            <section></section>
        </section>`;
        sheetContainer.appendChild(htmlToElement(sheetHtml));
    }

    function getButtonActions() {
        return {
            'clear': function(e) {
                requestAnimationFrame(function() {
                    if (hasLocalStorage) {
                        localStorage.clear();
                        location.reload();
                    }
                });
            },
            'print': function(e) {
                requestAnimationFrame(function() {
                    window.print();
                });
            },
            'addPage': function(e) {
                addPage();
                updatePageNumbers();
            }
        };
    }

    function addDocumentControls() {
        if (!hasTemplate) return false;
        var docControlsStr =
            `<!-- Document control buttons-->
            <div id="document-controls">
                <button data-action="clear" title="Remove saved draft">Clear draft</button>
                <a role="button" data-action="save" title="Download as HTML" id="download-link" download>Save HTML</a>
                <button data-action="print" title="Print">Print</button>
            </div>
            <div id="github-link">     
                <a class="github-button" href="https://github.com/Tombarr/html-resume-template" data-size="large" data-show-count="true" aria-label="Star Tombarr/html-resume-template on GitHub">Star</a>
            </div>`;
        var docControls = htmlToElement(docControlsStr);
        document.body.appendChild(docControls);
        return true;
    }

    var downloadLink = null;

    function bindDocumentControls() {
        var actions = getButtonActions();
        var docControls = document.getElementById('document-controls');
        if (!docControls) return false;
        var buttons = docControls.querySelectorAll('button[data-action]');
        for (var i = 0, e = buttons.length; i < e; i++) {
            if (buttons[i].dataset.action in actions) {
                buttons[i].addEventListener('click', actions[buttons[i].dataset.action]);
            }
        }
        downloadLink = docControls.querySelector('#download-link');
        if (!USE_CONTENTEDITABLE) {
            downloadLink.addEventListener('mouseover', setDesignMode('off'));
            downloadLink.addEventListener('mouseout', setDesignMode('on'));
            downloadLink.addEventListener('touchstart', setDesignMode('off'));
            downloadLink.addEventListener('touchend', setDesignMode('on'));
        }
        return true;
    }

    function updateDownloadLink() {
        if (!downloadLink) return;
        var pageContents = getPageContents();
        var objectUrl = getDownloadLink(pageContents, 'text/html; charset=UTF-8');
        downloadLink.setAttribute('href', objectUrl);
        downloadLink.setAttribute('download', 'resume.html'); // file name
    }

    function bindMutationObserver() {
        if (!('MutationObserver' in window) || !hasLocalStorage) return;

        function onMutate(mutations) {
            requestAnimationFrame(function() {
                savePage();
                updateMetadata();
                updateDownloadLink();
            });
        }

        var observer = new MutationObserver(onMutate);

        var config = {
            childList: true,
            characterData: true,
            subtree: true
        };

        observer.observe(document.body, config);
    }

    function makeEditable() {
        if (USE_CONTENTEDITABLE) {
            var editableNodes = document.querySelectorAll('p, span, ul.editable, ol.editable, ul:not(.editable) li, ol:not(.editable) li, time, h1, h2, h3, h4, h5, h6, address');
            for (var i = 0, e = editableNodes.length; i < e; i++) {
                var node = editableNodes[i];
                node.setAttribute('contenteditable', 'true');
                node.setAttribute('spellcheck', 'true');
            }
        } else {
            document.body.setAttribute('spellcheck', 'true');
            document.designMode = 'on';
        }

        if (hasLocalStorage) {
            document.body.addEventListener('focusout', savePage);
            document.body.addEventListener('focusin', savePage);
        }
    }

    function updatePageNumbers() {
        var pages = document.querySelectorAll('.sheet');
        for (var i = 0, e = pages.length; i < e; i++) {
            pages[i].setAttribute('data-page-number', i + 1);
        }
        document.body.setAttribute('data-page-count', pages.length);
    }

    // Source https://stackoverflow.com/questions/12409299/how-to-get-current-formatted-date-dd-mm-yyyy-in-javascript-and-append-it-to-an-i
    function getDateFormatted(inDate) {
        var today = new Date();
        var date = (inDate) ? inDate : today;
        var dd = date.getDate();
        var mm = date.getMonth()+1;
        var yyyy = date.getFullYear();

        // Pad day and month if needed
        if (dd < 10) {
            dd = '0'+dd;
        }

        if (mm < 10) {
            mm = '0'+mm;
        }

        return yyyy+'-'+mm+'-'+dd;
    }

    // Metadata

    function updateMetadata() {
        updateMetaDate();
        updateMetaSubject();
        updateMetaAuthor();
        updateMetaKeywords();
        updateTitle();
    }

    function updateMetaDate() {
        document.querySelector('meta[name="date"]').setAttribute('content', getDateFormatted());
    }

    function getSummary() {
        var summaryEl = document.querySelector('.summary > p');
        if (!(summaryEl && summaryEl.textContent)) return '';
        var summaryText = summaryEl.textContent.trim().replace(/(\r\n\t|\n|\r\t)/gm, " ").replace(/\s+/g, " ");
        return summaryText;
    }

    function getAuthor() {
        var authorEl = document.querySelector('.name');
        if (!(authorEl && authorEl.textContent)) return '';
        var authorName = authorEl.getAttribute('aria-label').trim();
        return authorName;
    }

    function getSkills() {
        var skillEls = document.querySelectorAll('.skills li');
        if (!(skillEls && skillEls.length)) return [];
        var skills = new Array(skillEls.length);
        for (var i = 0, e = skillEls.length; i < e; i++) {
            skills[i] = skillEls[i].textContent.trim();
        }
        return skills;
    }

    function updateMetaSubject() {
        var summaryText = getSummary();
        if ((!summaryText && summaryText.length)) return;
        document.querySelector('meta[name="subject"]').setAttribute('content', summaryText);
    }

    function updateMetaAuthor() {
        var authorName = getAuthor();
        if (!(authorName && authorName.length)) return;
        document.querySelector('meta[name="author"]').setAttribute('content', authorName);
    }

    function updateMetaKeywords() {
        var skills = getSkills();
        if (!(skills && skills.length)) return;
        document.querySelector('meta[name="keywords"]').setAttribute('content', skills.join(','));
    }

    function updateTitle() {
        var authorName = getAuthor();
        var summaryText = getSummary();
        if ((!summaryText && summaryText.length) || !(authorName && authorName.length)) return;
        document.title = authorName + " - " + summaryText;
    }

    if (hasLocalStorage) {
        restoreSavedPage();
        addDocumentControls();
        bindDocumentControls();
        updateMetadata();
        updateDownloadLink();
    }


    updatePageNumbers();
    makeEditable();
    requestAnimationFrame(bindMutationObserver);
})();

    function setContact(user) {
        var contact = document.getElementById('contact')
        var phone = document.createElement("li")
        phone.innerHTML = `<p><i class="fa fa-phone" title="Cell phone"></i> <a href="tel:${user.phone}">user.mobileNo</a></p>`
        var address = document.createElement("li")
        address.innerHTML = `<p><i class="fa fa-map-marker-alt" title="Location"></i>${user.address}</p>`
        var email = document.createElement("li")
        email.innerHTML = `<p><i class="fa fa-envelope" title="Email"></i> <a href="mailto:${user.email}">${user.email}</a></p>`
        contact.appendChild(address)
        contact.appendChild(phone)
        contact.appendChild(email)
        contact.appendChild()
    }

    function updateData(user) {
        var name = document.getElementById('name')




        var education = document.getElementById('education')
        var nameText = `<tspan x="54.728" y="174">${user.name}</tspan>`
        name.appendChild(nameText)
        setSkills(user)
        setTechnologies(user)
        setSummary(user)
        setProfile(user)
        setExperience(user)
    }

    function setExperience(user) {
        var experience = document.getElementById('experience')
    }

    function setProfile(user) {
        var profile = document.getElementById('profile')
        profile.innerHTML = user.job
    }

    function setSummary(user) {
        var summary = document.getElementById('summary')
        summary.innerHTML = user.summary
    }

    function setTechnologies(user) {
            var technologies = document.getElementById('technologies')
            mySkills = technologies.split(',')
            for(let i=0; i<mySkills.length(); i++) {
                var skill = document.createElement('li')
                skill.innerHTML = `<span>${mySkills[i]}</span>`
                technologies.appendChild(skill)
            }
        }
    function setSkills() {
        var skills = document.getElementById('skills')
        mySkills = skills.split(',')
        for(let i=0; i<mySkills.length(); i++) {
            var skill = document.createElement('li')
            skill.innerHTML = `<span>${mySkills[i]}</span>`
            skills.appendChild(skill)
        }
    }

    function getUser(uid){
        var docRef = db.collection("Users").doc(uid);

        docRef.get().then((doc) => {
            if (doc.exists) {
                updateData(doc.data);
            } else {
                // doc.data() will be undefined in this case
                console.log("No such document!");
            }
        }).catch((error) => {
            console.log("Error getting document:", error);
        });
    }

//    var url_string = window.location.href
//    var url = new URL(url_string);
//    var uid = url.searchParams.get("uid");
    getUser("nXJDnKEoJXN8swQ7yvGhkuavqvk2");