import axios from "axios";

export const addClub = (club) => {
  return (dispatch, getState) => {
    const token = getState().auth.token;
    return axios.post('/api/administration/clubs', {
      ...club
    }, {
      headers: {
        'x-auth': token
      }
    })
      .then((response) => {
        console.log(response.data.club_id);
      })
      .catch((e) => {
        console.log(e);
      });
  };
};