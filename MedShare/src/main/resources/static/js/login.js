// Configurações
const CONFIG = {
    API_ENDPOINTS: {
        GOOGLE_LOGIN: '/api/auth/google-login',
        GOOGLE_SIGNUP: '/api/auth/google-signup'
    },
    REDIRECT_URL: '/dashboard'
};

// Utilitários
const Utils = {
    showMessage: function(message, type = 'error') {
        const existingMessages = document.querySelectorAll('.error-message, .success-message');
        existingMessages.forEach(msg => msg.remove());

        const messageDiv = document.createElement('div');
        messageDiv.className = type === 'error' ? 'error-message' : 'success-message';
        messageDiv.textContent = message;

        const container = document.querySelector('.google-signin-section');
        if (container) {
            container.insertBefore(messageDiv, container.firstChild);
        }

        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 5000);
    },

    validateSignupForm: function() {
        const terms = document.getElementById('terms');

        if (terms && !terms.checked) {
            Utils.showMessage('Você deve aceitar os Termos de Uso para continuar');
            return false;
        }

        return true;
    },

    getSignupData: function() {
        const newsletter = document.getElementById('newsletter');
        return {
            newsletter: newsletter ? newsletter.checked : false
        };
    }
};

// Gerenciador de Autenticação Google
const GoogleAuth = {
    initialize: function() {
        if (typeof google !== 'undefined') {
            google.accounts.id.initialize({
                client_id: "SEU_GOOGLE_CLIENT_ID_AQUI",
                callback: this.handleCredentialResponse.bind(this)
            });
        }
    },

    handleCredentialResponse: function(response) {
        console.log("Token JWT recebido:", response.credential);

        const isSignup = window.location.pathname.includes('signup');

        if (isSignup) {
            this.handleSignup(response.credential);
        } else {
            this.handleLogin(response.credential);
        }
    },

    handleLogin: function(token) {
        this.setGoogleButtonLoading(true);

        fetch(CONFIG.API_ENDPOINTS.GOOGLE_LOGIN, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ token: token })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                Utils.showMessage('Login realizado com sucesso!', 'success');
                setTimeout(() => {
                    window.location.href = CONFIG.REDIRECT_URL;
                }, 1500);
            } else {
                Utils.showMessage(data.message || 'Erro no login. Tente novamente.');
            }
        })
        .catch(error => {
            console.error('Erro no login:', error);
            Utils.showMessage('Erro de conexão. Verifique sua internet e tente novamente.');
        })
        .finally(() => {
            this.setGoogleButtonLoading(false);
        });
    },

    handleSignup: function(token) {
        if (!Utils.validateSignupForm()) {
            return;
        }

        this.setGoogleButtonLoading(true);
        const signupData = Utils.getSignupData();

        fetch(CONFIG.API_ENDPOINTS.GOOGLE_SIGNUP, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                token: token,
                ...signupData
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                Utils.showMessage('Conta criada com sucesso! Bem-vindo ao MedShare!', 'success');
                setTimeout(() => {
                    window.location.href = CONFIG.REDIRECT_URL;
                }, 2000);
            } else {
                Utils.showMessage(data.message || 'Erro ao criar conta. Tente novamente.');
            }
        })
        .catch(error => {
            console.error('Erro no cadastro:', error);
            Utils.showMessage('Erro de conexão. Verifique sua internet e tente novamente.');
        })
        .finally(() => {
            this.setGoogleButtonLoading(false);
        });
    },

    setGoogleButtonLoading: function(isLoading) {
        const googleButton = document.querySelector('.g_id_signin');
        if (googleButton) {
            if (isLoading) {
                googleButton.style.opacity = '0.6';
                googleButton.style.pointerEvents = 'none';
            } else {
                googleButton.style.opacity = '1';
                googleButton.style.pointerEvents = 'auto';
            }
        }
    }
};

// Funções globais para callbacks do Google
window.handleCredentialResponse = function(response) {
    GoogleAuth.handleCredentialResponse(response);
};

window.handleGoogleSignup = function(response) {
    GoogleAuth.handleCredentialResponse(response);
};

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    console.log('Inicializando autenticação Google...');

    const checkGoogleAPI = setInterval(() => {
        if (typeof google !== 'undefined' && google.accounts) {
            GoogleAuth.initialize();
            clearInterval(checkGoogleAPI);
            console.log('Google Sign-In inicializado com sucesso');
        }
    }, 100);

    setTimeout(() => {
        clearInterval(checkGoogleAPI);
        if (typeof google === 'undefined') {
            console.warn('Google Sign-In API não carregou.');
            Utils.showMessage('Erro ao carregar autenticação Google. Recarregue a página.');
        }
    }, 10000);
});