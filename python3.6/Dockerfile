FROM python:3.6-slim-buster

ENV FLASK_PROXY_PORT 8080

COPY requirements.txt requirements.txt

RUN apt-get update \
        # Upgrade installed packages to get latest security fixes if the base image does not contain them already.
        && apt-get upgrade -y --no-install-recommends \
        # add some packages required for the pip install
        && apt-get install -y --no-install-recommends \
           gcc \
           libc-dev \
           libxslt-dev \
           libxml2-dev \
           libffi-dev \
           libssl-dev \
           zip \
           unzip \
           vim \
        # cleanup package lists, they are not used anymore in this image
        && rm -rf /var/lib/apt/lists/* \
        && apt-cache search linux-headers-generic \
        # install additional python modules
        #   we pin setuptools==57.5.0, reason is that ibm_db<=3.0.4 requires 'use_2to3' during install which has been
        #   removed from setuptools starting with the 58.x.x release. The install therefore fails with:
        #   error in ibm_db setup command: use_2to3 is invalid.
        #   When ibm_db is upgraded to a version that does not need 'use_2to3' anymore the pin can also be removed.
        && pip install --upgrade pip setuptools==57.5.0 six  \
        && pip install --no-cache-dir -r requirements.txt \
        # create action working directory
        && mkdir -p /action \
        && mkdir -p /actionProxy \
        && mkdir -p /pythonAction

ADD https://raw.githubusercontent.com/apache/openwhisk-runtime-docker/8b2e205c39d84ed5ede6b1b08cccf314a2b13105/core/actionProxy/actionproxy.py /actionProxy/actionproxy.py

ADD https://raw.githubusercontent.com/apache/openwhisk-runtime-python/3%401.0.3/core/pythonAction/pythonrunner.py /pythonAction/pythonrunner.py

CMD ["/bin/bash", "-c", "cd /pythonAction && python -u pythonrunner.py"]
