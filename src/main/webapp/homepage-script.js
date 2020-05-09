/* METADATA */
const API_ENDPOINT = '/rest';
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const searchterms = urlParams.get("searchterms");

document.addEventListener("DOMContentLoaded", async function () {
    let searchElement = document.querySelector("#search-form #tracktitle");
    searchElement.addEventListener("input", performSearch);
    if (searchterms) {
        searchElement.value = searchterms;
        searchElement.dispatchEvent(new Event("input"));
    }

    const featuredTracks = await fetchFeaturedTracks();
    updateFeaturedTracks(featuredTracks);
});

async function performSearch(event) {
    let element = event.target;
    let searchTerms = element.value;
    if (searchTerms && searchTerms.length >= 3) {
        const searchResults = await fetchSearchTracks(searchTerms);
        updateTrackResults(searchResults);
    } else {
        clearSearch();
    }
}
function clearSearch() {
    let resultsContainer = document.getElementById("track-search-results-container");
    resultsContainer.innerHTML = "";
}
function fetchSearchTracks(searchTerms) {
    let urlSearchParams = new URLSearchParams();
    urlSearchParams.set("searchterms", searchTerms);
    const url = `${API_ENDPOINT}/track/search?${urlSearchParams.toString()}`;
    const searchResults = fetch(url).then((data) => data.json());
    return searchResults;
}

function fetchFeaturedTracks() {
    const url = `${API_ENDPOINT}/track/featured`;
    const featuredTracks = fetch(url).then((data) => data.json());
    return featuredTracks;
}

function updateTrackResults(tracks) {
    let resultsContainer = document.getElementById("track-search-results-container");
    clearSearch();
    for (let track of tracks) {
        let trackSearchResultElement = createTrackLinkElement(track);
        resultsContainer.appendChild(trackSearchResultElement);
    }
}

function updateFeaturedTracks(tracks){
    let featuredTracksContainer = document.getElementById("featured-tracks-container");
    featuredTracksContainer.innerHTML = "";
    for (let track of tracks) {
        let featuredTrackElement = createTrackLinkElement(track);
        featuredTracksContainer.appendChild(featuredTrackElement);
    }
}

function createTrackLinkElement(track) {
    let trackLinkElement = document.createElement("a");
    trackLinkElement.classList.add("track-link");
    trackLinkElement.href = `/play?trackid=${track.trackId}`;

    let titleElement = document.createElement("span");
    titleElement.classList.add("track-link-title");
    titleElement.textContent = track.title;

    let artistElement = document.createElement("span");
    artistElement.classList.add("track-link-artist");
    artistElement.textContent = track.artist;

    let yearElement = document.createElement("span");
    yearElement.classList.add("track-link-year");
    yearElement.textContent = track.year;

    trackLinkElement.appendChild(titleElement);
    trackLinkElement.innerHTML += " — ";
    trackLinkElement.appendChild(artistElement);
    trackLinkElement.innerHTML += " (";
    trackLinkElement.appendChild(yearElement);
    trackLinkElement.innerHTML += ")";

    return trackLinkElement;
}
