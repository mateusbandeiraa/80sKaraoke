/* METADATA */
const API_ENDPOINT = '/rest';
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const searchterms = urlParams.get("searchterms");

document.addEventListener("DOMContentLoaded", async function () {
    let searchElement = document.querySelector("#search-form #tracktitle");
    searchElement.addEventListener("input", performSearch);
    if(searchterms){
        searchElement.value = searchterms;
        searchElement.dispatchEvent(new Event("input"));
    }
});

function performSearch(event) {
    let element = event.target;
    let searchTerms = element.value;
    if (searchTerms && searchTerms.length >= 3) {
        fetchSearchTracks(searchTerms);
    } else {
        clearSearch();
    }
}
function clearSearch(){
    let resultsContainer = document.getElementById("track-search-results-container");
    resultsContainer.innerHTML = "";
}
async function fetchSearchTracks(searchTerms) {
    let urlSearchParams = new URLSearchParams();
    urlSearchParams.set("searchterms", searchTerms);
    const url = `${API_ENDPOINT}/track/search?${urlSearchParams.toString()}`;
    const searchResults = await fetch(url).then((data) => data.json());
    updateTrackResults(searchResults);
}

function updateTrackResults(tracks) {
    let resultsContainer = document.getElementById("track-search-results-container");
    clearSearch();
    for (let track of tracks) {
        let trackSearchResultElement = document.createElement("a");
        trackSearchResultElement.classList.add("track-search-result");
        trackSearchResultElement.href = `/play?trackid=${track.trackId}`;

        let titleElement = document.createElement("span");
        titleElement.classList.add("track-search-result-title");
        titleElement.textContent = track.title;

        let artistElement = document.createElement("span");
        artistElement.classList.add("track-search-result-artist");
        artistElement.textContent = track.artist;

        let yearElement = document.createElement("span");
        yearElement.classList.add("track-search-result-year");
        yearElement.textContent = track.year;

        trackSearchResultElement.appendChild(titleElement);
        trackSearchResultElement.innerHTML += " — ";
        trackSearchResultElement.appendChild(artistElement);
        trackSearchResultElement.innerHTML += " (";
        trackSearchResultElement.appendChild(yearElement);
        trackSearchResultElement.innerHTML += ")";
        resultsContainer.appendChild(trackSearchResultElement);
    }
}