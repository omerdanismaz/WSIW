document.getElementById("recommendation-form").addEventListener("submit", function(event)
{
    event.preventDefault();

    const informationMessage = document.getElementById("information-message");
    const type = document.querySelector('input[name="type"]:checked');
    const category = document.querySelector('input[name="category"]:checked');
    const dateRange = document.querySelector('input[name="dateRange"]:checked');
    const ratingSite = document.querySelector('input[name="ratingSite"]:checked');

    if(!type)
    {
        informationMessage.textContent = messageOne;
        informationMessage.classList.remove("hidden");
    }
    else if(!category)
    {
        informationMessage.textContent = messageTwo;
        informationMessage.classList.remove("hidden");
    }
    else if(!dateRange)
    {
        informationMessage.textContent = messageThree;
        informationMessage.classList.remove("hidden");
    }
    else if(!ratingSite)
    {
        informationMessage.textContent = messageFour;
        informationMessage.classList.remove("hidden");
    }
    else
    {
        informationMessage.classList.add("hidden");
        document.getElementById("recommendation-form").submit();
    }
});
